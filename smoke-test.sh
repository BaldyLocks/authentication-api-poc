#!/bin/bash

curl --cacert conf/ca.pem --cert conf/pkey.pem --key conf/pkey.key --pass changeit -d '{"username":"milos", "password":"1234567", "accountNumber":"77853449"}' -H "Content-Type: application/json" https://localhost:8084/register-account

curl --cacert conf/ca.pem --cert conf/pkey.pem --key conf/pkey.key --pass changeit -d '{"username":"milos", "password":"1234567"}' -H "Content-Type: application/json" https://localhost:8084/authenticate
