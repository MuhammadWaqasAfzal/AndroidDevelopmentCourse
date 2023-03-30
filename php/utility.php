<?php


include('dbcon.php');

function getUsersList()
{
    global $conn;

    $query = "SELECT * FROM Users ";
    $query_run = mysqli_query($conn, $query);
    if ($query_run) {
        if (mysqli_num_rows($query_run) > 0) {
            $res = mysqli_fetch_all($query_run, MYSQLI_ASSOC);
            $data = [
                'status' => 200,
                'message' => "User Found Successfully",
                'data' => $res
            ];
            header("HTTP/1.0 200 Success");
            return json_encode($data);
        } else {
            return response("404", "HTTP/1.0 404 No User Found", "No User Found");
        }
    } else {
        return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
    }
}




function registerUser($userData)
{
    global $conn;

    $firstName = $userData['firstName'];
    $lastName =  $userData['lastName'];
    $email = $userData['email'];
    $password =  $userData['password'];
    $confirmPassowrd = $userData['confirmPassword'];
    $isAdmin = $userData['admin'];
    $gender = $userData['gender'];


    if (empty(trim($firstName))) {
        return response("422", "HTTP/1.0 422 First name is required", "First name is required");
    } else if (empty(trim($lastName))) {
        return response("422", "HTTP/1.0 422 Last name is required", "Last name is required");
    } else if (empty(trim($email))) {
        return response("422", "HTTP/1.0 422 Email is required", "Email is required");
    } else if (empty(trim($password))) {
        return response("422", "HTTP/1.0 422 Password is required", "Password is required");
    } else if (empty(trim($confirmPassowrd))) {
        return response("422", "HTTP/1.0 422 Confirm password is required", "Confirm password is required");
    } else if ((trim($password)) !== trim($confirmPassowrd)) {
        return response("422", "HTTP/1.0 422 Passwords do not match", "Passwords do not match");
    }
    // else if (empty(trim((string)$gender))) {
    //     return response("422", "HTTP/1.0 422 Gender is required", "Gender is required");
    // }else if (empty(trim((string)$isAdmin))) {
    //     return response("422", "HTTP/1.0 422 Admin is required", "Admin is required");
    // }
    else {
        $userExits = "SELECT * FROM Users WHERE email = '$email'";
        $query_run = $conn->query($userExits);
        if (mysqli_num_rows($query_run) > 0) {
            $data = [
                'status' => 404,
                'message' => "Email Already Exits",
            ];
            header("HTTP/1.0 404 Email Already Exits");
            return json_encode($data);
        } else {
            $query = "INSERT INTO Users (FirstName,LastName,Password,Email,Gender,Admin)
            VALUES ('$firstName','$lastName','$password','$email','$gender','$isAdmin')";
            $query_run = mysqli_query($conn, $query);
            if ($query_run) {
                $query = "SELECT * FROM Users WHERE email = '$email' AND password = '$password'";
                // $result = $conn -> query($userExits);
                $result = mysqli_query($conn, $query);
                if ($result) {
                    $res = mysqli_fetch_assoc($result);
                    $data = [
                        'status' => 201,
                        'message' => "User Registered Successfully",
                        'data' => $res
                    ];
                    header("HTTP/1.0 201 Success");
                    return json_encode($data);
                } else {
                    return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
                }
                // return response("201", "HTTP/1.0 201 Created", "User Registered Successfully");
            } else {
                return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
            }
        }
    }
}

function loginUser($userData)
{
    global $conn;

    $email = mysqli_real_escape_string($conn, $userData['email']);
    $password = mysqli_real_escape_string($conn, $userData['password']);

    if (empty(trim($email))) {
        return response("422", "HTTP/1.0 422 Email is required", "Email is required");
    } else if (empty(trim($password))) {
        return response("422", "HTTP/1.0 422 Password is required", "Password is required");
    } else {
        $query = "SELECT * FROM Users WHERE email = '$email' AND password = '$password'";
        // $result = $conn -> query($userExits);
        $result = mysqli_query($conn, $query);
        if ($result) {
            if (mysqli_num_rows($result) > 0) {
                $res = mysqli_fetch_assoc($result);
                //echo $row;

                $data = [
                    'status' => 200,
                    'message' => "User logged in Successfully",
                    'data' => $res
                ];
                header("HTTP/1.0 200 Success");
                return json_encode($data);
            } else {
                $data = [
                    'status' => 401,
                    'message' => "Invalid username or password",
                ];
                header("HTTP/1.0 401 Unauthorized");
                return json_encode($data);
            }
        } else {
            return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
        }
    }
}

function resetPassword($userData)
{
    global $conn;

    $email = mysqli_real_escape_string($conn, $userData['email']);
    $password = mysqli_real_escape_string($conn, $userData['password']);
    $confirmPassowrd = mysqli_real_escape_string($conn, $userData['confirmPassword']);

    if (empty(trim($email))) {
        return response("422", "HTTP/1.0 422 Email is required", "Email is required");
    } else if (empty(trim($password))) {
        return response("422", "HTTP/1.0 422 Password is required", "Password is required");
    } else if (empty(trim($confirmPassowrd))) {
        return response("422", "HTTP/1.0 422 Confirm password is required", "Confirm password is required");
    } else if ((trim($password)) !== trim($confirmPassowrd)) {
        return response("422", "HTTP/1.0 422 Passwords do not match", "Passwords do not match");
    } else {
        $userExits = "SELECT * FROM Users WHERE email = '$email'";
        $query_run = $conn->query($userExits);
        if (mysqli_num_rows($query_run) > 0) {
            $query_run = null;
            $query = "UPDATE `Users` SET `Password`='$password' WHERE Email = '$email'";
            $query_run = mysqli_query($conn, $query);
            if ($query_run) {
                return response("200", "Password Updated Successfully", "Password Updated Successfully");
            } else {
                return response("500", "Internal Server Error", "Internal Server Error");
            }
        } else {
            return response("404", "HTTP/1.0 404 Invalid Email", "Invalid Email");
        }
    }
}


function addReview($userData)
{
    global $conn;

    $description = mysqli_real_escape_string($conn, $userData['description']);
    $email = mysqli_real_escape_string($conn, $userData['email']);
    // $userName = mysqli_real_escape_string($conn, $userData['userName']);
    // $id = mysqli_real_escape_string($conn, $userData['id']);
    $likes = 0;
    $disLikes = 0;
    date_default_timezone_set('London');
    $date = date('m/d/Y h:i:s a', time());

    if (empty(trim($email))) {
        return response("422", "HTTP/1.0 422 Email is required", "Email is required");
    } else if (empty(trim($description))) {
        return response("422", "HTTP/1.0 422 Reviews description cannot be empty", "Reviews description cannot be empty");
    } else {
        $userExits = "SELECT * FROM Users WHERE email = '$email'";
        $query_run = $conn->query($userExits);
        if (mysqli_num_rows($query_run) > 0) {
            $res = mysqli_fetch_assoc($query_run);

            $userName = (string)$res["FirstName"] . " " . (string) $res["LastName"];
            $userId = $res["Id"];
            $query = "INSERT INTO Reviews (Description,Email,Likes,Dislikes,DateAndTime,UserName,UserId)
            VALUES ('$description','$email','$likes','$disLikes','$date' , '$userName','$userId')";
            $query_run = mysqli_query($conn, $query);
            if ($query_run) {
                return response("201", "HTTP/1.0 201 Review created Successfully", "Review created Successfully");
            } else {
                return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
            }
        } else {
            return response("404", "HTTP/1.0 404 Duplicate", "Invalid Email");
        }
    }
}


function sendMessage($userData)
{
    global $conn;

    $text = $userData['text'];
    $senderId = $userData['senderId'];
    $receiverId =  $userData['receiverId'];
    date_default_timezone_set('London');
    $date = date('m/d/Y h:i:s a', time());

    if (empty((trim($text)))) {
        return response("422", "HTTP/1.0 422 Message cannot be empty.", "Message cannot be empty");
    } else if (empty(trim((string)$senderId))) {
        return response("422", "HTTP/1.0 422 SenderId cannot be empty", "SenderId cannot be empty");
    } else if (empty(trim((string)$receiverId))) {
        return response("422", "HTTP/1.0 422 ReceiverId cannot be empty", "ReceiverId cannot be empty");
    } else {
        $query = "INSERT INTO Messages (SenderId,ReceiverId,TextMessage,DateAndTime)
            VALUES ('$senderId','$receiverId','$text','$date' )";
        $query_run = mysqli_query($conn, $query);
        if ($query_run) {
            return response("201", "HTTP/1.0 201 Message sent Successfully", "Message sent Successfully");
        } else {
            return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
        }
    }
}

function updateReview($userData)
{
    global $conn;

    $description = mysqli_real_escape_string($conn, $userData['description']);
    $email = mysqli_real_escape_string($conn, $userData['email']);
    $id = mysqli_real_escape_string($conn, $userData['id']);
    date_default_timezone_set('London');
    $date = date('m/d/Y h:i:s a', time());

    if (empty(trim($email))) {
        return response("422", "HTTP/1.0 422 Email is required", "Email is required");
    } else if (empty(trim($description))) {
        return response("422", "HTTP/1.0 422 Reviews description cannot be empty", "Reviews description cannot be empty");
    } else if (empty(trim($id))) {
        return response("422", "HTTP/1.0 422 Review id is required", "Review id is required");
    } else {
        $query = "UPDATE `Reviews` SET `Description`='$description' ,`DateAndTime`='$date' WHERE Id = '$id'";
        $query_run = mysqli_query($conn, $query);
        if ($query_run) {
            return response("200", "HTTP/1.0 200 Review Updated Successfully", "Review Updated Successfully");
        } else {
            return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
        }
    }
}

function likeDisLikeReview($userData)
{
    global $conn;

    $email = mysqli_real_escape_string($conn, $userData['email']);
    $reviewId = $userData['reviewId'];
    $userId = $userData['userId'];
    $reaction = $userData['reaction'];

    if (empty(trim($email))) {
        return response("422", "HTTP/1.0 422 Email is required", "Email is required");
    } else if (empty(trim($reviewId))) {
        return response("422", "HTTP/1.0 422 Id is required", "Id is required");
    } else {

        $reviewExit = "SELECT * FROM Reviews WHERE id = '$reviewId'";
        $query_run = $conn->query($reviewExit);

        if (mysqli_num_rows($query_run) > 0) {
            $res = mysqli_fetch_assoc($query_run);
            $queryAllreadyreacted = "SELECT * FROM LikesDisLikes Where UserID= '$userId' AND ReviewId = '$reviewId'";
            $query_run_all_ready_reacted = mysqli_query($conn, $queryAllreadyreacted);
            if (mysqli_num_rows($query_run_all_ready_reacted) > 0) {
                //$res = mysqli_fetch_assoc($query_run_all_ready_reacted);
                if ($reaction == 1) {
                    $dislikes = $res["Dislikes"] - 1;
                    $query = "UPDATE `Reviews` SET `Dislikes`='$dislikes' WHERE Id = '$reviewId'";
                    $query_run = mysqli_query($conn, $query);
                    $likes = $res["Likes"] + 1;
                    $query = "UPDATE `Reviews` SET `Likes`='$likes' WHERE Id = '$reviewId'";
                    $query_run = mysqli_query($conn, $query);
                } else {
                    $likes = $res["Likes"] - 1;
                    $query = "UPDATE `Reviews` SET `Likes`='$likes' WHERE Id = '$reviewId'";
                    $query_run = mysqli_query($conn, $query);
                    $dislikes = $res["Dislikes"] + 1;
                    $query = "UPDATE `Reviews` SET `Dislikes`='$dislikes' WHERE Id = '$reviewId'";
                    $query_run = mysqli_query($conn, $query);
                }
            } else {
                if ($reaction == 1) {
                    $likes = $res["Likes"] + 1;
                    $query = "UPDATE `Reviews` SET `Likes`='$likes' WHERE Id = '$reviewId'";
                    $query_run = mysqli_query($conn, $query);
                } else {
                    $dislikes = $res["Dislikes"] + 1;
                    $query = "UPDATE `Reviews` SET `Dislikes`='$dislikes' WHERE Id = '$reviewId'";
                    $query_run = mysqli_query($conn, $query);
                }
            }

            if ($query_run) {
                $query = "SELECT * FROM LikesDisLikes Where `UserId`= '$userId' AND `ReviewId` = '$reviewId'";
                $query_run = mysqli_query($conn, $query);

                if (mysqli_num_rows($query_run) > 0) {
                    $query = "UPDATE `LikesDisLikes`  SET `Reaction`='$reaction' WHERE `UserId`='$userId' AND `ReviewId` = '$reviewId' ";
                    $query_run = mysqli_query($conn, $query);
                } else {
                    $query =  "INSERT INTO LikesDisLikes (UserId,Reaction,ReviewId) VALUES ('$userId','$reaction',$reviewId)";
                    $query_run = mysqli_query($conn, $query);
                }
               // sleep(2);
                $query = "SELECT * FROM REVIEWS WHERE id = '$reviewId'" ;
             
                $query_run = mysqli_query($conn, $query);
                $res = mysqli_fetch_all($query_run, MYSQLI_ASSOC);

               

                $row = $res[0];
                $reviewId =  $row['Id'];
               
                $query = "SELECT * FROM LikesDisLikes where ReviewId='$reviewId' ";
                $query_run = mysqli_query($conn, $query);
                $likesRes = mysqli_fetch_all($query_run, MYSQLI_ASSOC);
                $res[0]["Reactions"] = $likesRes; 
                

                
                if ($reaction == 1)
                   $message =  "Review Liked Successfully";
                else
                    $message = "Review DisLiked Successfully";
                $data = [
                    'status' => 200,
                    'message' => $message,
                    'data' => $res
                ];
               
                header("HTTP/1.0 200 Success");
                //echo(json_encode($data));
                return json_encode($data);
                
            } else {
                return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
            }
        } else {
            return response("404", "HTTP/1.0 404 Invalid", "Invalid Review Id");
        }
    }
}

function getAllReviews()
{
    global $conn;
    $query = "SELECT * FROM REVIEWS ORDER BY DateAndTime DESC";
    $query_run = mysqli_query($conn, $query);

    if ($query_run) {
        $res = mysqli_fetch_all($query_run, MYSQLI_ASSOC);

        for ($x = 0; $x < count($res); $x++) {
            $row = $res[$x];
            $reviewId =  $row['Id'];
            $query = "SELECT * FROM LikesDisLikes where ReviewId='$reviewId' ";
            $query_run = mysqli_query($conn, $query);
            $likesRes = mysqli_fetch_all($query_run, MYSQLI_ASSOC);
            $res[$x]["Reactions"] = $likesRes; 
        }

        $data = [
            'status' => 200,
            'message' => "All Reviews",
            'data' => $res
        ];
        header("HTTP/1.0 200 Success");
        return json_encode($data);
    } else {
        return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
    }
}

function disLikeReview($userData)
{
    global $conn;

    $email = mysqli_real_escape_string($conn, $userData['email']);
    $reviewId = mysqli_real_escape_string($conn, $userData['reviewId']);
    $userId = mysqli_real_escape_string($conn, $userData['userId']);

    if (empty(trim($email))) {
        return response("422", "HTTP/1.0 422 Email is required", "Email is required");
    } else if (empty(trim($reviewId))) {
        return response("422", "HTTP/1.0 422 Id is required", "Id is required");
    } else {
        $reviewExit = "SELECT * FROM Reviews WHERE id = '$reviewId'";
        $query_run = $conn->query($reviewExit);

        if (mysqli_num_rows($query_run) > 0) {
            $res = mysqli_fetch_assoc($query_run);
            $disLikes = $res["Dislikes"] + 1;
            $query = "UPDATE `Reviews` SET `Dislikes`='$disLikes' WHERE Id = '$reviewId'";
            $query_run = mysqli_query($conn, $query);
            if ($query_run) {
                $query =  "INSERT INTO LikesDisLikes (UserId,Reaction) VALUES ('$reviewId',0)";
                $query_run = mysqli_query($conn, $query);
                return response("200", "HTTP/1.0 200 Review DisLiked Successfully", "Review DisLiked Successfully");
            } else {
                return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
            }
        } else {
            return response("404", "HTTP/1.0 404 Invalid", "Invalid Review Id");
        }
    }
}

function deleteReview($userData)
{
    global $conn;

    $email = mysqli_real_escape_string($conn, $userData['email']);
    $id = mysqli_real_escape_string($conn, $userData['id']);

    if (empty(trim($email))) {
        return response("422", "HTTP/1.0 422 Email is required", "Email is required");
    } else if (empty(trim($id))) {
        return response("422", "HTTP/1.0 422 Id is required", "Id is required");
    } else {
        $reviewExit = "SELECT * FROM Reviews WHERE id = '$id'";
        $query_run = $conn->query($reviewExit);

        if (mysqli_num_rows($query_run) > 0) {

            $query = "DELETE FROM `Reviews` WHERE Id = '$id'";
            $query_run = mysqli_query($conn, $query);
            if ($query_run) {
                return response("200", "HTTP/1.0 201 Created", "Review Deleted Successfully");
            } else {
                return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
            }
        } else {
            return response("404", "HTTP/1.0 404 Invalid", "Invalid Review Id");
        }
    }
}





function getAllUsers($data)
{
    global $conn;
    $userId = mysqli_real_escape_string($conn, $data['userId']);
    $query = "SELECT * FROM Users Where id!='$userId'";
    $query_run = mysqli_query($conn, $query);

    if ($query_run) {
        $res = mysqli_fetch_all($query_run, MYSQLI_ASSOC);
        
        for ($x = 0; $x < count($res); $x++) {
            $row = $res[$x];
            $senderId =  $row['SenderId'];
            $receiverId =  $row['ReceiverId'];
            $query = "SELECT * FROM Users where Id='$senderId' ";
            $query_run = mysqli_query($conn, $query);
            $userRes = mysqli_fetch_all($query_run, MYSQLI_ASSOC);
            $res[$x]["SenderName"] =  $userRes[0]["FirstName"] ." ". $userRes[0]["LastName"] ;
            $res[$x]["SenderGender"] =  $userRes[0]["Gender"];

            $query = "SELECT * FROM Users where Id='$receiverId' ";
            $query_run = mysqli_query($conn, $query);
            $userRes = mysqli_fetch_all($query_run, MYSQLI_ASSOC);
            $res[$x]["ReceiverName"] =  $userRes[0]["FirstName"] . " ".$userRes[0]["LastName"] ;
            $res[$x]["ReceiverGender"] =  $userRes[0]["Gender"];


        }

        $data = [
            'status' => 200,
            'message' => "All Users",
            'data' => $res
        ];
        header("HTTP/1.0 200 Success");
        return json_encode($data);
    } else {
        return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
    }
}

function getAllMessages($data)
{
    global $conn;
    $senderId = mysqli_real_escape_string($conn, $data['userId']);
    if(empty(trim($senderId)))
        $query = "SELECT * FROM Messages  ORDER BY DateAndTime DESC ";
    else
        $query = "SELECT * FROM Messages where SenderId='$senderId' OR ReceiverId='$senderId' ORDER BY DateAndTime DESC ";
    $query_run = mysqli_query($conn, $query);

    if ($query_run) {
        $res = mysqli_fetch_all($query_run, MYSQLI_ASSOC);

       
        for ($x = 0; $x < count($res); $x++) {
            $row = $res[$x];
            $senderId =  $row['SenderId'];
            $receiverId =  $row['ReceiverId'];
            $query = "SELECT * FROM Users where Id='$senderId' ";
            $query_run = mysqli_query($conn, $query);
            $userRes = mysqli_fetch_all($query_run, MYSQLI_ASSOC);
            $res[$x]["SenderName"] =  $userRes[0]["FirstName"] ." ". $userRes[0]["LastName"] ;
            $res[$x]["SenderGender"] =  $userRes[0]["Gender"];

            $query = "SELECT * FROM Users where Id='$receiverId' ";
            $query_run = mysqli_query($conn, $query);
            $userRes = mysqli_fetch_all($query_run, MYSQLI_ASSOC);
            $res[$x]["ReceiverName"] =  $userRes[0]["FirstName"] . " ".$userRes[0]["LastName"] ;
            $res[$x]["ReceiverGender"] =  $userRes[0]["Gender"];


        }
     
        $data = [
            'status' => 200,
            'message' => "All Messages",
            'data' => $res
        ];
        header("HTTP/1.0 200 Success");
        return json_encode($data);
    } else {
        return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
    }
}




function response($statusCode, $header, $message)
{
    $data = [
        'status' => $statusCode,
        'message' => $message,
    ];
    header($header);
    echo json_encode($data);
    exit();
}
