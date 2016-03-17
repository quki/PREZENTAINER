<?php

    $host="localhost";
    $uname="cyh1704";
    $pwd="cy34208263";
    $db="cyh1704";
    //// DB정보

    $con = mysql_connect($host,$uname,$pwd) or die("connection failed");
    mysql_select_db($db,$con) or die("db selection failed");
     mysql_set_charset("utf8");

    /////DB연결 및 한글 인코딩


    $id =$_POST['yourId'];
    $tit=$_POST['title'];
    $dat = $_POST['date'];

   //// Post형식으로 Parameter 전달받음
  

    mysql_query( "delete from android2  where email = '".$id."' and title = '".$tit."' and date = '".$dat."' ");
   /////  테이블에 id, title, date 가 같은 요소를 delete

    print(json_encode($flag));
    mysql_close($con);
?>
