<?php

include('utility.php');

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Allow-Headers: access");

$requestMethod = $_SERVER['REQUEST_METHOD'];

if($requestMethod == "GET")
{
   // print("Waqas");
    $usersList = getUsersList();
    print($usersList);

}else{
    $data = [
        'status' => 405,
        'message' => $requestMethod , " Method Not Allowed",
    ];

    header("HTTP/1.0 405 Method Not Allowed");
    echo json_encode($data);
}

?>