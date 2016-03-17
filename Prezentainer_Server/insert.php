<?php

    $host="localhost";
    $uname="cyh1704";                           
    $pwd="cy34028263";
    $db="cyh1704";

    ////db 정보


    $con = mysql_connect($host,$uname,$pwd) or die("connection failed");       
    mysql_select_db($db,$con) or die("db selection failed");

    ////db 연결

                            
     mysql_set_charset("utf8");

    ////한글 인코딩

    $email1 =$_POST['email'];
    $time1=$_POST['time'];
    $title1 = $_POST['title'];
    $hbr1 = $_POST['hbr'];
    $date1 = $_POST['date'];
     //// POST로 전달받은 Parameter 저장

    mysql_query("insert into android2(email, time, title, hbr,date) values('$email1','$time1','$title1','$hbr1','$date1') ");
    ///// DB에 inserting

    print(json_encode($flag));
    mysql_close($con);
?>
