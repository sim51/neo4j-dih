'use strict';

app.controller('MenuCtrl', ['$scope', '$location', 'DIH', function ($scope, $location, DIH) {

    DIH.listing().then(function(result){
        $scope.files = result;
    });

    $scope.isActive = function (viewLocation) {
        return viewLocation === $location.path();
    };

}]);
