package com.eundaeng.schooltable

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.intellij.lang.annotations.RegExp
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.URI
import java.net.URL

class Comci {
    /**
     * comcigan-parser Module
     *
     * index.js
     *
     * Github : https://github.com/leegeunhyeok/comcigan-parser
     * NPM : https://www.npmjs.com/package/comcigan-parser
     *
     * @description 컴시간 시간표 파싱 라이브러리
     * @author Leegeunhyeok
     * @license MIT
     */
    val HOST = "http://컴시간학생.kr"
    private var _baseUrl: String? = null
    private lateinit var _url: String
    private var _initialized: Boolean = false
    private var _pageSource: String? = null
    private var _cache = null
    private var _cacheAt = null
    private var _schoolCode = -1
    private var _weekdayString = arrayOf('일', '월', '화', '수', '목', '금', '토')
    private var _option = arrayOf(3,0) //maxGrade, cache)

    private val uri = Jsoup.connect(HOST).get().select("html > frameset > frame[src]").attr("src")

    lateinit var _scData: List<String>
    lateinit var _extractCode: String
    fun init(op: Array<Int>): String {
        _option = op
        _url = uri
        val url = URL(uri)
        _baseUrl = url.protocol + "://" + url.host + ":" + if(url.port == -1) 80 else url.port
        val source = Jsoup.connect(_url).get().html()
        val idx = source.indexOf("school_ra(sc)");
        val idx2 = source.indexOf("sc_data('");

        if (idx == -1 || idx == -1) {
            return "소스에서 식별 코드를 찾을 수 없습니다.";
        }

        val extractSchoolRa = source.substring(idx, 50).replace(" ", "")
        val schoolRa = Regex("url:'.(.*?)'").findAll(extractSchoolRa)

        // sc_data 인자값 추출
        val extractScData = source.substring(idx2, 30).replace(" ", "")
        val scData = Regex("\\(.*?\\)").findAll(extractScData)

        if (scData.count() != 0) {
            _scData = scData.toList()[0].value.replace(Regex("[()]"), "").replace(Regex("'"), "").split(',');
        } else {
            return "sc_data 값을 찾을 수 없습니다."
        }


        if (schoolRa.count() != 0) {
            _extractCode = schoolRa.toList()[1].value
        } else {
            return "school_ra 값을 찾을 수 없습니다.";

        }
            _pageSource = source
            _initialized = true
            return "true"
        }

        /**
         * 시간표 데이터를 불러올 학교를 설정합니다.
         *
         * @param {string} keyword 학교 검색 키워드
         * @returns 검색된 학교 목록 `Array<[코드, 지역, 학교이름, 학교코드]>`
         */
        @Throws(Error::class)
        fun search(keyword: String): JSONArray {
            if (!this._initialized) {
                throw Error("초기화가 진행되지 않았습니다.");
            }

            var hexString = "";
            for (i in keyword) {
                hexString += "%$i";
            }

            val body = Jsoup.connect(this._baseUrl + this._extractCode + hexString).get().html()
            val jsonString = body.substring(0, body.lastIndexOf('}') + 1)
            val searchData = JSONObject(jsonString).getJSONObject("학교검색")

            if (searchData.length() <= 0) {
                throw Error("검색된 학교가 없습니다.")
            }

            val arr = JSONArray()
            for(i in 0..searchData.length()) {
                arr.put(
                    i, JSONObject(
                        """
                            "_": ${searchData.get("0")},
                            "region": ${searchData.get("1")},
                            "name": ${searchData.get("2")},
                            "code": ${searchData.get("3")},
                        """
                    )
                )
            }
            return arr
        }

        /**
         * 시간표를 조회할 학교 코드를 등록합니다
         *
         * @param school
         */
        fun setSchool(schoolCode: Int) {
            this._schoolCode = schoolCode;
            this._cache = null;
        }

        /**
         * 설정한 학교의 전교 시간표 데이터를 불러옵니다
         *
         * @return 시간표 데이터
         */
        suspend fun getTimetable() {
            this._isReady();

            // 캐시 지속시간이 존재하고, 아직 만료되지 않았다면 기존 값 전달
            // 만료되었거나, 캐시가 비활성화(기본값)되어있는 경우엔 항상 새로운 값 파싱하여 전달
            if (this._option[1] != 0 && !this._isCacheExpired()) {
                return this._cache
            }

            val jsonString: String = this._getData();
            val resultJson = JSONObject(jsonString);
            val startTag = Regex("""<script language(.*?)>""").findAll(this._pageSource!!).toList()[0].value
            val regex = Regex("$startTag(.*?)</script>", RegexOption.valueOf("gi"))

            var match: String
            var script = "";
            // 컴시간 웹 페이지 JS 코드 추출
            while ((match = regex.exec(this._pageSource))) {
                script += match[1];
            }

            // 데이터 처리 함수명 추출
            val functioName = script
                    .match(/function 자료[^\(]*/gm)[0]
            .replace(/\+s/, '')
            .replace('function', '');

            // 학년 별 전체 학급 수
            val classCount = resultJson['학급수'];

            // 시간표 데이터 객체
            val timetableData = {};

            // 1학년 ~ maxGrade 학년 교실 반복
            for (let grade = 1; grade <= this._option['maxGrade']; grade++) {
                if (!timetableData[grade]) {
                    timetableData[grade] = {};
                }

                // 학년 별 반 수 만큼 반복
                for (let classNum = 1; classNum <= classCount[grade]; classNum++) {
                if (!timetableData[grade][classNum]) {
                    timetableData[grade][classNum] = {};
                }

                timetableData[grade][classNum] = this._getClassTimetable(
                    { data: jsonString, script, functioName },
                    grade,
                    classNum,
                );
            }
            }

            this._cache = timetableData;
            this._cacheAt = +new Date();
            return timetableData;
        }

        /**
         * 교시별 수업시간 정보를 조회합니다.
         * @returns
         */
        suspend fun getClassTime() {
            this._isReady();
            // 교시별 시작/종료 시간 데이터
            return JSON.parse(await this._getData())['일과시간'];
        }

        /**
         * 컴시간의 API를 통해 전체 시간표 데이터를 수집/파싱하여 반환합니다.
         */
        async _getData() {
            val da1 = '0';
            val s7 = this._scData[0] + this._schoolCode;
            val sc3 =
            this._extractCode.split('?')[0] +
                    '?' +
                    Buffer.from(s7 + '_' + da1 + '_' + this._scData[2]).toString('base64');

            // JSON 데이터 로드
            val jsonString = await new Promise((resolve, reject) => {
                request(this._baseUrl + sc3, (err, _ㄴres, body) => {
                if (err) {
                    reject(err);
                }

                if (!body) {
                    reject(new Error('시간표 데이터를 찾을 수 없습니다.'));
                }

                // String to JSON
                resolve(body.substr(0, body.lastIndexOf('}') + 1));
            });
            });

            return jsonString;
        }

        /**
         * 지정된 학년/반의 1주일 시간표를 파싱합니다
         *
         * @param codeConfig 데이터, 함수명, 소스코드 객체
         * @param grade 학년
         * @param classNumber 반
         * @returns
         */
        _getClassTimetable(codeConfig, grade, classNumber) {
            val args = [codeConfig.data, grade, classNumber];
            val call = codeConfig.functioName + '(' + args.join(',') + ')';
            val script = codeConfig.script + '\n\n' + call;

            /** DEAD: Sorry about using eval() **/
            val res = eval(script);

            // Table HTML script
            val $ = cheerio.load(res);
            val $this = this;
            val timetable = [];
            $('tr').each(function (timeIdx) {
                val currentTime = timeIdx - 2;
                // 1, 2번째 tr은 제목 영역이므로 스킵
                if (timeIdx <= 1) return;

                $(this)
                .find('td')
                .each(function (weekDayIdx) {
                    val currentWeekDay = weekDayIdx - 1;
                    // 처음(제목)과 끝(토요일) 영역은 스킵
                    if (weekDayIdx === 0 || weekDayIdx === 6) return;

                    if (!timetable[currentWeekDay]) {
                        timetable[currentWeekDay] = [];
                    }

                    val subject = $(this).contents().first().text();
                    val teacher = $(this).contents().last().text();
                    timetable[currentWeekDay][currentTime] = {
                        grade,
                        class: classNumber,
                            weekday: weekDayIdx - 1,
                        weekdayString: $this._weekdayString[weekDayIdx],
                        classTime: currentTime + 1,
                        teacher,
                        subject,
                    };
                });
            });

            return timetable;
        }

        /**
         * 초기화 및 학교 설정이 모두 준비되었는지 확인합니다.
         */
        fun _isReady() {
            if (!this._initialized) {
                throw new Error('초기화가 진행되지 않았습니다.');
            }

            if (this._schoolCode === -1) {
                throw new Error('학교 설정이 진행되지 않았습니다.');
            }
        }

        /**
         * 사용자가 세팅한 캐시 지속 시간을 확인하여 만료 여부를 반환합니다.
         *
         * @returns 캐시 만료 여부
         */
        _isCacheExpired() {
            return +new Date() - this._cacheAt >= this._option.cache;
        }
    }
}