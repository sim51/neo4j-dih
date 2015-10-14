'use strict';

app.controller('ImportCtrl', ['$scope', '$routeParams', 'DIH', function ($scope, $routeParams, DIH) {

    $scope.name = $routeParams.name;
    $scope.configuration = true;
    $scope.import = true;

    DIH.getConfig($scope.name).then(function(result){
        $scope.config = result;
    });

    DIH.getConfig($scope.name.replace('.xml', '.properties')).then(function(result){
        if(result.indexOf("{ 'ERROR'") == -1){
            $scope.properties = result;
        }
    });

    $scope.fnImport = function() {
        $scope.inProgress = true;
        DIH.import($scope.clean, $scope.debug, $scope.name).then(function(result){
            $scope.result = JSON.stringify(result, null, 2);
            $scope.inProgress = false;
        });
    }

    $scope.fnToggleConfiguration = function() {
        if($scope.configuration) {
            $scope.configuration = false;
        }
        else {
            $scope.configuration = true;
        }
    };
    $scope.fnToggleImport = function() {
        if($scope.import) {
            $scope.import = false;
        }
        else {
            $scope.import = true;
        }
    };
}]);
