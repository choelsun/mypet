    // 상세 지역 정보를 담고 있는 객체
    const cat2 = {
        '서울': ['강남구', '강동구', '강북구', '강서구'],
        '부산': ['강서구', '금정구', '남구', '동구', '동래구', '부산진구', '북구', '사상구', '사하구', '서구', '수영구', '연제구', '영도구', '중구', '해운대구', '기장군'],
        '대구': ['남구', '달서구', '동구', '북구', '서구', '수성구', '중구', '달성군'],
        '인천': ['계양구', '남구', '남동구', '동구', '부평구', '서구', '연수구', '중구', '강화군', '옹진군'],
        '광주': ['광산구', '남구', '동구', '북구', '서구'],
        '대전': ['대덕구', '동구', '서구', '유성구', '중구'],
        '울산': ['남구', '동구', '북구', '중구', '울주군'],
        '강원': ['강릉시', '동해시', '삼척시', '속초시', '원주시', '춘천시', '태백시', '고성군', '양구군', '양양군', '영월군', '인제군', '정선군', '철원군', '평창군', '홍천군', '화천군', '횡성군'],
        '경기': ['고양시 덕양구', '고양시 일산구', '과천시', '광명시', '광주시', '구리시', '군포시', '김포시', '남양주시', '동두천시', '부천시 소사구', '부천시 오정구', '부천시 원미구', '성남시 분당구', '성남시 수정구', '성남시 중원구', '수원시 권선구', '수원시 장안구', '수원시 팔달구', '시흥시', '안산시 단원구', '안산시 상록구', '안성시', '안양시 동안구', '안양시 만안구', '오산시', '용인시', '의왕시', '의정부시', '이천시', '파주시', '평택시', '하남시', '화성시', '가평군', '양주군', '양평군', '여주군', '연천군', '포천군'],
        '경남': ['거제시', '김해시', '마산시', '밀양시', '사천시', '양산시', '진주시', '진해시', '창원시', '통영시', '거창군', '고성군', '남해군', '산청군', '의령군', '창녕군', '하동군', '함안군', '함양군', '합천군'],
        '경북': ['경산시', '경주시', '구미시', '김천시', '문경시', '상주시', '안동시', '영주시', '영천시', '포항시 남구', '포항시 북구', '고령군', '군위군', '봉화군', '성주군', '영덕군', '영양군', '예천군', '울릉군', '울진군', '의성군', '청도군', '청송군', '칠곡군'],
        '전남': ['광양시', '나주시', '목포시', '순천시', '여수시', '강진군', '고흥군', '곡성군', '구례군', '담양군', '무안군', '보성군', '신안군', '영광군', '영암군', '완도군', '장성군', '장흥군', '진도군', '함평군', '해남군', '화순군'],
        '전북': ['군산시', '김제시', '남원시', '익산시', '전주시 덕진구', '전주시 완산구', '정읍시', '고창군', '무주군', '부안군', '순창군', '완주군', '임실군', '장수군', '진안군'],
        '제주': ['서귀포시', '제주시', '남제주군', '북제주군'],
        '충남': ['공주시', '논산시', '보령시', '서산시', '아산시', '천안시', '금산군', '당진군', '부여군', '서천군', '연기군', '예산군', '청양군', '태안군', '홍성군'],
        '충북': ['제천시', '청주시 상당구', '청주시 흥덕구', '충주시', '괴산군', '단양군', '보은군', '영동군', '옥천군', '음성군', '진천군', '청원군']
    };

	// 지역 선택에 따른 상세 지역 목록 변경 함수 (전역으로 등록)
	window.CityArea_change = function(selectedCityArea) {
	    const detailArea = document.getElementById('detailArea');

	    if (!detailArea) {
	        console.error("detailArea element not found");
	        return;
	    }

	    detailArea.length = 0;
	    detailArea.options[0] = new Option('-선택-', '');

	    // 지역 선택에 따른 상세 지역 설정
	    const subCities = cat2[selectedCityArea];
	    if (subCities && Array.isArray(subCities)) {
	        for (let i = 0; i < subCities.length; i++) {
	            detailArea.options[detailArea.options.length] = new Option(subCities[i], subCities[i]);
	        }
	    }

	    console.log(`CityArea: ${selectedCityArea}, SubCities: ${subCities}`);
	}

	// DOMContentLoaded 이벤트 리스너로 HTML 요소가 준비된 후에 코드 실행
	document.addEventListener('DOMContentLoaded', function () {
	    const cityAreaElement = document.getElementById('CityArea');

	    if (cityAreaElement) {
	        cityAreaElement.addEventListener('change', function (event) {
	            CityArea_change(event.target.value);
	        });
	    }

	    const selectedCityArea = /*[[${loggedInUser.CityArea}]]*/ '';
	    const selectedDetailArea = /*[[${loggedInUser.detailArea}]]*/ '';

	    if (selectedCityArea && selectedCityArea !== '-선택-') {
	        cityAreaElement.value = selectedCityArea;
	        CityArea_change(selectedCityArea);

	        if (selectedDetailArea && selectedDetailArea !== '-선택-') {
	            const detailAreaElement = document.getElementById('detailArea');
	            if (detailAreaElement) {
	                detailAreaElement.value = selectedDetailArea;
	            }
	        }
	        console.log(`CityArea: ${selectedCityArea}, SubCities: ${cat2[selectedCityArea]}`);
	    }
	});