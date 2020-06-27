function checkFile(flag) {
    let file;
    if (flag == 1) {
        let file = document.getElementById('file1');
    }
    else {
        let file = document.getElementById('file2');
    }

    if (file.value.trim() == "") {
        alert("please choose the file!");
        return false;
    } else {
        return true;
    }
}

function checkInput() {
    let id = document.getElementById('id');
    let amount = document.getElementById('amount');
    if (id.value.trim() == "" || amount.value.trim() == "") {
        alert("please input the id and amount!");
        return false;
    } else {
        return true;
    }
}

function checkUser() {
    let USERID = document.getElementById('USERID');
    let PASS = document.getElementById('PASS');
    if (USERID.value.trim() == "" || PASS.value.trim() == "") {
        alert("please input user info!");
        return false;
    } else {
        return true;
    }
}

