let sentCode; // 발송된 인증 코드를 저장할 변수

// 인증 코드 발송 함수
function sendVerificationCode() {
    const email = document.getElementById("email").value;
    const domain = document.getElementById("domain").value;
    const fullEmail = `${email}@${domain}`;

    if (!email || !domain) {
        alert("이메일과 도메인을 작성(선택)해주세요");
        return; // 이메일과 도메인이 없으면 반환
    }

    fetch(`/api/mail/confirm.json?email=${encodeURIComponent(email)}&domain=${encodeURIComponent(domain)}`, {
        method: 'POST'
    })
    .then(response => response.text())
    .then(code => {
        sentCode = code; // 발송된 인증 코드 저장
        alert(`인증 코드가 ${fullEmail}로 발송되었습니다`);
        document.getElementById("verificationMessage").textContent = ""; // 이전 메시지 초기화
    })
    .catch(error => {
        console.error('Error in sendVerificationCode:', error);
        alert('인증 코드 발송에 실패했습니다.');
    });
}

// 이메일 중복 검사 함수
function checkEmail() {
    const email = document.getElementById("email").value;
    const domain = document.getElementById("domain").value;

    if (!email || !domain) {
        document.getElementById("emailMessage").textContent = "이메일과 도메인을 모두 입력(선택)해 주세요.";
        document.getElementById("emailMessage").style.color = "red";
        return;
    }

    const fullEmail = `${email}@${domain}`;

    fetch('/member/check-email', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email: fullEmail })
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                console.error('Error response:', text);
                throw new Error('서버 오류: ' + text);
            });
        }
        return response.json();
    })
    .then(data => {
        if (data.exists) {
            document.getElementById("emailMessage").textContent = "이미 사용 중인 이메일입니다.";
            document.getElementById("emailMessage").style.color = "red";
        } else {
            document.getElementById("emailMessage").textContent = "사용 가능한 이메일입니다.";
            document.getElementById("emailMessage").style.color = "green";
        }
    })
    .catch(error => {
        console.error('Error in checkEmail:', error);
        document.getElementById("emailMessage").textContent = "서버 오류. 다시 시도해주세요.";
    });
}

// 비밀번호 유효성 검사 함수
function validatePassword() {
    const passwordElement = document.getElementById("password");
    const passwordMessageElement = document.getElementById("passwordMessage");
    const password = passwordElement.value;

    // 비밀번호 정규식: 4~16자리의 알파벳, 숫자, 특수문자를 포함
    const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+])[a-zA-Z\d!@#$%^&*()_+]{4,16}$/;

    if (password === "") {
        passwordMessageElement.textContent = ""; // 입력이 없으면 메시지 초기화
        return;
    }

    if (!passwordRegex.test(password)) {
        passwordMessageElement.textContent = "비밀번호는 특수문자, 숫자, 알파벳 포함 4~16글자로 입력해 주세요.";
        passwordMessageElement.style.color = "red";
    } else {
        passwordMessageElement.textContent = "사용 가능한 비밀번호입니다.";
        passwordMessageElement.style.color = "green";
    }
}

// 비밀번호 확인 유효성 검사 함수
function validateConfirmPassword() {
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;
    const confirmPasswordMessageElement = document.getElementById("confirmPasswordMessage");

    if (confirmPassword === "") {
        confirmPasswordMessageElement.textContent = ""; // 입력이 없으면 메시지 초기화
        return;
    }

    if (password !== confirmPassword) {
        confirmPasswordMessageElement.textContent = "비밀번호가 일치하지 않습니다.";
        confirmPasswordMessageElement.style.color = "red";
    } else {
        confirmPasswordMessageElement.textContent = "비밀번호가 일치합니다.";
        confirmPasswordMessageElement.style.color = "green";
    }
}

// 인증 코드 확인 함수
function checkVerificationCode() {
    const verificationCode = document.getElementById("verificationCode").value;

    if (verificationCode === "") {
        document.getElementById("verificationMessage").textContent = "";
        return; // 입력이 없으면 메시지 초기화
    }

    if (verificationCode !== sentCode) {
        document.getElementById("verificationMessage").textContent = "인증 코드가 일치하지 않습니다.";
        document.getElementById("verificationMessage").style.color = "red";
        return false; // 인증 코드 불일치
    } else {
        document.getElementById("verificationMessage").textContent = "인증 코드가 일치합니다.";
        document.getElementById("verificationMessage").style.color = "green";
        return true; // 인증 코드 일치
    }
}

// 이메일 입력란에 변화가 생길 때마다 중복 검사 수행
document.getElementById("email").addEventListener("input", checkEmail);
document.getElementById("domain").addEventListener("input", checkEmail);
// 인증 코드 입력 시 확인
document.getElementById("verificationCode").addEventListener("input", checkVerificationCode);
// 비밀번호 입력란을 벗어날 때 비밀번호 유효성 검사 수행
document.getElementById("password").addEventListener("blur", validatePassword);
// 비밀번호 확인 입력란을 벗어날 때 비밀번호 확인 유효성 검사 수행
document.getElementById("confirmPassword").addEventListener("blur", validateConfirmPassword);
// 회원가입 폼 제출 이벤트 리스너
document.getElementById("signupForm").addEventListener("submit", function(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    // 필요한 요소들을 모두 찾습니다.
    const emailElement = document.getElementById("email");
    const domainElement = document.getElementById("domain");
    const verificationCodeElement = document.getElementById("verificationCode");
    const passwordElement = document.getElementById("password");
    const confirmPasswordElement = document.getElementById("confirmPassword");
    const nameElement = document.getElementById("name");
    const phoneElement = document.getElementById("phone");
    const cityAreaElement = document.getElementById("CityArea");
    const detailAreaElement = document.getElementById("detailArea");

    // 유효성 검사 수행
    if (!emailElement.value || !domainElement.value) {
        alert("이메일과 도메인을 입력해 주세요.");
        return;
    }
    if (!verificationCodeElement.value) {
        alert("인증 코드를 입력해 주세요.");
        return;
    }
    if (!passwordElement.value) {
        alert("비밀번호를 입력해 주세요.");
        return;
    }
    if (!confirmPasswordElement.value) {
        alert("비밀번호 확인을 입력해 주세요.");
        return;
    }
    if (passwordElement.value !== confirmPasswordElement.value) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
    }
    if (!nameElement.value) {
        alert("이름을 입력해 주세요.");
        return;
    }
    if (!phoneElement.value) {
        alert("핸드폰 번호를 입력해 주세요.");
        return;
    }
    if (!cityAreaElement.value || !detailAreaElement.value) {
        alert("지역 정보를 입력해 주세요.");
        return;
    }

    // 인증 코드 확인
    if (!checkVerificationCode()) {
        alert("인증 코드가 일치하지 않습니다.");
        return;
    }

    // 모든 유효성 검사가 통과되었을 때 폼 데이터 전송
    const formData = new FormData();
    formData.append("email", `${emailElement.value}@${domainElement.value}`);
    formData.append("verificationCode", verificationCodeElement.value);
    formData.append("password", passwordElement.value);
    formData.append("name", nameElement.value);
    formData.append("phone", phoneElement.value);
    formData.append("area", cityAreaElement.value);
    formData.append("detailArea", detailAreaElement.value);

    fetch('/member/signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams(formData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('회원가입 실패');
        }
        alert('회원가입 성공');
        window.location.href = '/main'; // 성공 시 메인 페이지로 이동
    })
    .catch(error => {
        console.error('회원가입 중 오류 발생:', error);
        alert('회원가입 중 오류 발생: ' + error.message);
    });
});
