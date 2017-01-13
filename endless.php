<?php
	
function doNothing(&$param){
	echo "I am really doing nothing\n";
}


$params = array(
	"hello" => "world"
);

for ($i=0; $i<count($params); $i++){
	doNothing($params[$i]);
}

