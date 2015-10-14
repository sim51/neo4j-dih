'use strict';

/**
 * Neo4j service.
 */
app.service('DIH', ['$http', '$q',  function ($http, $q) {

    //var url = 'http://localhost:7475';
    var url = '';

    this.ping = function() {
        var request = {
            method: 'GET',
            url: url + '/dih/api/ping',
            headers: {
                'Accept': 'application/json',
                'Content-type': 'application/json; charset=utf-8'
            }
        };
        var def = $q.defer();
        $http(request)
            .then(function successCallback(response) {
                def.resolve(response.data)
            }, function errorCallback(response) {
                throw new Error('Bad return value (' + response.status + ') for REST API. \n\nRequest :' + JSON.stringify(response) + '\n\nResponse :' + JSON.stringify(response.data));
            });
        return def.promise;
    };

    this.getConfig = function(name) {
        var request = {
            method: 'GET',
            url: url + '/dih/api/get?name=' + name,
            headers: {
                'Accept': 'application/json',
                'Content-type': 'application/json; charset=utf-8'
            }
        };
        var def = $q.defer();
        $http(request)
            .then(function successCallback(response) {
                def.resolve(response.data)
            }, function errorCallback(response) {
                def.resolve(" { 'ERROR' : " + JSON.stringify(response) + "}");
            });
        return def.promise;
    };

    this.listing = function() {
        var request = {
            method: 'GET',
            url: url + '/dih/api/listing',
            headers: {
                'Accept': 'application/json',
                'Content-type': 'application/json; charset=utf-8'
            }
        };
        var def = $q.defer();
        $http(request)
            .then(function successCallback(response) {
                def.resolve(response.data)
            }, function errorCallback(response) {
                throw new Error('Bad return value (' + response.status + ') for REST API. \n\nRequest :' + JSON.stringify(response) + '\n\nResponse :' + JSON.stringify(response.data));
            });
        return def.promise;
    };

    this.import = function(clean, debug, name){
        var request = {
            method: 'POST',
            url: url + '/dih/api/import',
            headers: {
                'Accept': 'application/json',
                'Content-type': 'application/json; charset=utf-8'
            },
            data: "clean=" + clean + "&debug=" + debug + "&name=" + name
        };
        var def = $q.defer();
        $http(request)
            .then(function successCallback(response) {
                def.resolve(response.data)
            }, function errorCallback(response) {
                def.resolve(response.data);
            });
        return def.promise;
    };

}]);
