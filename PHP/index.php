<?php

	include("encrypt.php");
	include("rsa_generate.php");


	if ($_SERVER['REQUEST_METHOD'] === 'POST') {
	    try {
	        $rawInput = file_get_contents('php://input');
	        $requestData = json_decode($rawInput, true);
	        $paramJson = $requestData['param'] ?? null;
	        $param = json_decode($paramJson, true);
		    if (!$param) {
		        throw new Exception("Missing 'param' in the request.");
		    }	

	        if (!isset($param['data'], $param['iv'], $param['key'])) {
	            throw new Exception("Invalid request data.");
	        }

	        $privateKeyPath = './private_key.pem';
	        $aesKey = decryptRSA($param['key'], $privateKeyPath);
	        $decryptedData = decryptAES($param['data'], $aesKey, $param['iv']);

			getListVpnEC($aesKey);
	        // header('Content-Type: application/json');
	        // echo json_encode([
	        //     'status' => 'success',
	        //     'decryptedData' => $decryptedData,
	        // ]);
	    } catch (Exception $e) {
	        header('Content-Type: application/json', true, 400);
	        echo json_encode([
	            'status' => 'error',
	            'message' => $e->getMessage(),
	        ]);
	    }
	} else {
	    header('Content-Type: application/json', true, 405);
	    echo json_encode([
	        'status' => 'error',
	        'message' => "Invalid request method. Only POST is allowed." . $_SERVER['REQUEST_METHOD'],
	    ]);
	}
?>