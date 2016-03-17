<?php 



 
$host="localhost";
$dbid="cyh1704";
$dbpass="cy34208263";
$dbname="cyh1704";
 /// DB정보
 
 $rs=mysql_connect($host,$dbid,$dbpass);
 mysql_set_charset("utf8");
 mysql_select_db($dbname);
//// DB연결 및 한글인코딩

 $id = $_POST['yourId'];
 $tit = $_POST['title'];
 $result = '{"myJson":"err"}';
 $dat = $_POST['date'];

//// POST형식으로 넘어온 Parameter를 저장


 $ss = "select * from android2  where date = '".$dat."' and email = '".$id."' and  title = '".$tit."' ";

 $rr = mysql_query($ss,$rs);

 //// 테이블에서 date, id, title 이 모두 일치하는 요소를 가지고옴



 if(!$rr) die("쿼리 실패 입니다.".mysql_error());
 mysql_set_charset("utf8");
 
 $result = '';
 
 $c = 0;
 
 while($row = mysql_fetch_array($rr)){
  
 if($c > 0) $result .= ",";
 
    
     $result .= '{"time":"'.$row[time].'","hbr":"'.$row[hbr].'"}';


 $c++;

 }   
 

/////// time과 hbr로 이루어진 Json 형성
 $result .= '';
 
 mysql_close($rs);
 
 echo($result);
 ////// Json을 안드로이드로 전달

?> 