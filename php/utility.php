<?php


include('dbcon.php');

function getUsersList()
{
    global $conn;
    $query = "SELECT * FROM Users";
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

    $firstName = mysqli_real_escape_string($conn, $userData['firstName']);
    $lastName = mysqli_real_escape_string($conn, $userData['lastName']);
    $email = mysqli_real_escape_string($conn, $userData['email']);
    $password = mysqli_real_escape_string($conn, $userData['password']);
    $confirmPassowrd = mysqli_real_escape_string($conn, $userData['confirmPassword']);

    if (empty(trim($firstName))) {
        return response("422", "HTTP/1.0 422 Data Validation", "First name is required");
    } else if (empty(trim($lastName))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Last name is required");
    } else if (empty(trim($email))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Email is required");
    } else if (empty(trim($password))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Password is required");
    } else if (empty(trim($confirmPassowrd))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Confirm password is required");
    } else if ((trim($password)) !== trim($confirmPassowrd)) {
        return response("422", "HTTP/1.0 422 Data Validation", "Passwords do not match");
    } else {
        $userExits = "SELECT * FROM Users WHERE email = '$email'";
        $query_run = $conn->query($userExits);
        if (mysqli_num_rows($query_run) > 0) {
            return response("404", "HTTP/1.0 404 Duplicate", "Email Already Exits");
        } else {
            $query = "INSERT INTO Users (FirstName,LastName,Password,Email)
            VALUES ('$firstName','$lastName','$password','$email')";
            $query_run = mysqli_query($conn, $query);
            if ($query_run) {
                return response("201", "HTTP/1.0 201 Created", "User Registered Successfully");
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
        return response("422", "HTTP/1.0 422 Data Validation", "Email is required");
    } else if (empty(trim($password))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Password is required");
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
                return response("201", "HTTP/1.0 401 Unauthorized", "Invalid username or password");
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
        return response("422", "HTTP/1.0 422 Data Validation", "Email is required");
    } else if (empty(trim($password))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Password is required");
    } else if (empty(trim($confirmPassowrd))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Confirm password is required");
    } else if ((trim($password)) !== trim($confirmPassowrd)) {
        return response("422", "HTTP/1.0 422 Data Validation", "Passwords do not match");
    } else {
        $userExits = "SELECT * FROM Users WHERE email = '$email'";
        $query_run = $conn->query($userExits);
        if (mysqli_num_rows($query_run) > 0) {
            $query_run = null;
            $query = "UPDATE `Users` SET `Password`='$password' WHERE Email = '$email'";
            $query_run = mysqli_query($conn, $query);
            if ($query_run) {
                return response("200", "HTTP/1.0 201 Created", "Password Updated Successfully");
            } else {
                return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
            }
        } else {
            return response("404", "HTTP/1.0 404 Duplicate", "Invalid Email");
        }
    }
}


function addReview($userData)
{
    global $conn;

    $description = mysqli_real_escape_string($conn, $userData['description']);
    $email = mysqli_real_escape_string($conn, $userData['email']);
    $likes = 0;
    $disLikes = 0;
    date_default_timezone_set('London');
    $date = date('m/d/Y h:i:s a', time());

    if (empty(trim($email))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Email is required");
    } else if (empty(trim($description))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Reviews description cannot be empty");
    } else {
        $userExits = "SELECT * FROM Users WHERE email = '$email'";
        $query_run = $conn->query($userExits);
        if (mysqli_num_rows($query_run) > 0) {

            $query = "INSERT INTO Reviews (Description,Email,Likes,Dislikes,DateAndTime)
            VALUES ('$description','$email','$likes','$disLikes','$date')";
            $query_run = mysqli_query($conn, $query);
            if ($query_run) {
                return response("201", "HTTP/1.0 201 Created", "Review created Successfully");
            } else {
                return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
            }
        } else {
            return response("404", "HTTP/1.0 404 Duplicate", "Invalid Email");
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
        return response("422", "HTTP/1.0 422 Data Validation", "Email is required");
    } else if (empty(trim($description))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Reviews description cannot be empty");
    } else if (empty(trim($id))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Review id is required");
    } else {
        $query = "UPDATE `Reviews` SET `Description`='$description' ,`DateAndTime`='$date' WHERE Id = '$id'";
        $query_run = mysqli_query($conn, $query);
        if ($query_run) {
            return response("200", "HTTP/1.0 201 Created", "Review Updated Successfully");
        } else {
            return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
        }
    }
}

function likeReview($userData)
{
    global $conn;

    $email = mysqli_real_escape_string($conn, $userData['email']);
    $id = mysqli_real_escape_string($conn, $userData['id']);

    if (empty(trim($email))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Email is required");
    } else if (empty(trim($id))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Id is required");
    } else {
        $reviewExit = "SELECT * FROM Reviews WHERE id = '$id'";
        $query_run = $conn->query($reviewExit);

        if (mysqli_num_rows($query_run) > 0) {
            $res = mysqli_fetch_assoc($query_run);
            $likes = $res["Likes"] + 1;
            $query = "UPDATE `Reviews` SET `Likes`='$likes' WHERE Id = '$id'";
            $query_run = mysqli_query($conn, $query);
            if ($query_run) {
                return response("200", "HTTP/1.0 201 Created", "Review Liked Successfully");
            } else {
                return response("500", "Internal Server Error", "HTTP/1.0 500 Internal Server Error");
            }
        } else {
            return response("404", "HTTP/1.0 404 Invalid", "Invalid Review Id");
        }
    }
}

function disLikeReview($userData)
{
    global $conn;

    $email = mysqli_real_escape_string($conn, $userData['email']);
    $id = mysqli_real_escape_string($conn, $userData['id']);

    if (empty(trim($email))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Email is required");
    } else if (empty(trim($id))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Id is required");
    } else {
        $reviewExit = "SELECT * FROM Reviews WHERE id = '$id'";
        $query_run = $conn->query($reviewExit);

        if (mysqli_num_rows($query_run) > 0) {
            $res = mysqli_fetch_assoc($query_run);
            $disLikes = $res["Dislikes"] + 1;
            $query = "UPDATE `Reviews` SET `Dislikes`='$disLikes' WHERE Id = '$id'";
            $query_run = mysqli_query($conn, $query);
            if ($query_run) {
                return response("200", "HTTP/1.0 201 Created", "Review DisLiked Successfully");
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
        return response("422", "HTTP/1.0 422 Data Validation", "Email is required");
    } else if (empty(trim($id))) {
        return response("422", "HTTP/1.0 422 Data Validation", "Id is required");
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


function getAllReviews()
{
    global $conn;
    $query = "SELECT * FROM REVIEWS";
    $query_run = mysqli_query($conn, $query);

    if ($query_run) {
        $res = mysqli_fetch_all($query_run, MYSQLI_ASSOC);

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
