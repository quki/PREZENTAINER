<?php 



 
 $host="localhost";
$dbid="cyh1704";
$dbpass="cy34208263";
$dbname="cyh1704";
 ////DB 정보
 
 $rs=mysql_connect($host,$dbid,$dbpass);
 mysql_set_charset("utf8");
 mysql_select_db($dbname);

////DB연결 및 한글 인코딩


 $id = $_POST['yourId'];

//// POST 형식으로 넘어온 parameter 를 전달받음

 $result = '{"myJson":"err"}';
 //$yourid = "이병근";


 $ss = "select * from android2  where email = '".$id."' "

$rr = mysql_query($ss,$rs);

///  DB테이블에서 계정정보가 같은 요소의 값을 꺼내옴.


 if(!$rr) die("쿼리 실패 입니다.".mysql_error());
 mysql_set_charset("utf8");
 
 $result = '[';
 
 $c = 0;
 
 while($row = mysql_fetch_array($rr)){
  
 if($c > 0) $result .= ",";
 
    
      $result .= '{"title":"'.$row[title].'","date":"'.$row[date].'","hbr":"'.$row[hbr].'"}';


 $c++;

 }   
 
 
 $result .= ']';
 

//// title, date, hbr 로 이루어진 Json을 형성

 mysql_close($rs);
 
 echo($result);

///// Json 정보 안드로이드로 전달 

?>

