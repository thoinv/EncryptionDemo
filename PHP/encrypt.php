<?php
function decodeBase64($base64String)
{
    $decodedData = base64_decode($base64String, true);

    if ($decodedData === false) {
        echo "Invalid Base64 input!";
        return "Invalid Base64 input!";
    } else {
        echo "Decoded Data: " . $decodedData;
        return $decodedData;
    }
}

function decryptDataWithAES($encryptedData, $aesKey, $iv)
{
    // Ensure the AES key and IV are in binary format (not base64 or hex)
    $key = $aesKey;
    $iv = $iv;

    $decryptedData = openssl_decrypt($encryptedData, 'AES-256-CBC', $key, OPENSSL_RAW_DATA, $iv);

    return $decryptedData;
}


function decryptRSA($encryptedData, $privateKeyPath)
{
    // Ensure the private key file exists
    if (!file_exists($privateKeyPath)) {
        throw new Exception("Private key file not found at: $privateKeyPath");
    }

    // Load the private key
    $privateKey = file_get_contents($privateKeyPath);
    if ($privateKey === false) {
        throw new Exception("Failed to read private key file.");
    }

    // Decode the Base64-encoded encrypted data
    $encryptedBinaryData = base64_decode($encryptedData, true);
    if ($encryptedBinaryData === false) {
        throw new Exception("Failed to decode Base64 encrypted data.");
    }

    // Decrypt the data using the private key
    $decryptedData = '';
    $decryptionSuccess = openssl_private_decrypt(
        $encryptedBinaryData,
        $decryptedData,
        $privateKey,
        OPENSSL_PKCS1_PADDING
    );

    if (!$decryptionSuccess) {
        throw new Exception("Decryption failed. Ensure the data and private key are correct.");
    }

    return $decryptedData;
}

function encryptDataWithAES($data, $aesKey)
{
    $key = $aesKey;

    $iv = openssl_random_pseudo_bytes(16);

    $encryptedData = openssl_encrypt($data, 'AES-256-CBC', $key, OPENSSL_RAW_DATA, $iv);

    if ($encryptedData === false) {
        throw new Exception("Encryption failed");
    }

    $encryptedData = base64_encode($encryptedData);
    $encodedIV = base64_encode($iv);

    return [$encryptedData, $encodedIV];
}

function decryptAES($encryptedData, $aesKey, $iv)
{
    // Decode Base64-encoded encrypted data and IV
    $decodedEncryptedData = base64_decode($encryptedData, true);
    $decodedIV = base64_decode($iv, true);

    if ($decodedEncryptedData === false) {
        throw new Exception("Failed to decode Base64 encrypted data.");
    }

    if ($decodedIV === false) {
        throw new Exception("Failed to decode Base64 IV.");
    }

    $decryptedData = openssl_decrypt(
        $decodedEncryptedData,  
        'AES-256-CBC',
        $aesKey,               
        OPENSSL_RAW_DATA,       
        $decodedIV              
    );

    if ($decryptedData === false) {
        throw new Exception("Failed to decrypt data using AES. Ensure the AES key and IV are correct.");
    }

    return $decryptedData;
}

function decryptAESKey($encryptedAesKey)
{
    $encryptedData = decodeBase64($encryptedAesKey);
    $privateKeyPath = './private_key.pem';

    if (!file_exists($privateKeyPath)) {
        throw new Exception("Private key file not found.");
    }

    $key = file_get_contents($privateKeyPath);
    $decodedKey = '';
    $decryptionSuccess = openssl_private_decrypt(
        $encryptedData,
        $decodedKey,
        $key,
        OPENSSL_PKCS1_PADDING
    );

    if (!$decryptionSuccess) {
        throw new Exception("Decryption failed.");
    }

    return $decodedKey;
}

function getListVpnEC($keyAES)
{
    // Simulate fetching data from a database (replace this with actual database queries)
    $listVpn = [
        ['online' => 1, 'test_vpn_server' => 0, 'max_connection' => 0, 'current_connection' => 0, 'cpu' => 10],
        ['online' => 1, 'test_vpn_server' => 0, 'max_connection' => 100, 'current_connection' => 50, 'cpu' => 30]
    ];

    $msg = 'OK!';
    foreach ($listVpn as &$vpn) {
        if ($vpn['max_connection'] == 0) {
            $vpn['quality'] = 100;
        } else {
            $vpn['quality'] = 100 - ((int)($vpn['cpu']));
        }
    }

    $response = [
        'code' => 0,
        'message' => $msg,
        'data' => $listVpn
    ];

    $resJson = json_encode($response);

    // Decrypt the AES key
    // $keyAES = decryptAESKey($encryptedAesKey);

    // Encrypt the response
    list($encryptedData, $encodedIV) = encryptDataWithAES($resJson, $keyAES);

    // Return the response in JSON format
    header('Content-Type: application/json');
    echo json_encode([
        'encryptedData' => $encryptedData,
        'iv' => $encodedIV
    ]);
}
?>
