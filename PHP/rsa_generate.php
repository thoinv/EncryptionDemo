<?php
require 'vendor/autoload.php';

use phpseclib3\Crypt\RSA;

function generateRSAKeyPair($privateKeyPath = null, $publicKeyPath = null, $keySize = 2048) {
    // Generate RSA key pair
    $keyPair = RSA::createKey($keySize);

    // Get the private and public keys
    $privateKey = $keyPair->toString('PKCS8'); // Private Key in PKCS8 format
    $publicKey = $keyPair->getPublicKey()->toString('PKCS8'); // Public Key in PKCS8 format

    // Save the keys to files if paths are provided
    if ($privateKeyPath) {
        file_put_contents($privateKeyPath, $privateKey);
    }
    if ($publicKeyPath) {
        file_put_contents($publicKeyPath, $publicKey);
    }

    // Return the keys as an array
    return [
        'privateKey' => $privateKey,
        'publicKey' => $publicKey,
    ];
}

// Paths for key files
$privateKeyPath = __DIR__ . '/private_key.pem';
$publicKeyPath = __DIR__ . '/public_key.pem';
?>
 