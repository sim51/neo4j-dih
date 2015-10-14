/**
 * Custom `exception` module that managed exception.
 */
angular.module('exception', []).factory('$exceptionHandler', function () {
    return function (exception, cause) {
        var $rootScope = $injector.get("$rootScope");
        $rootScope.addError({message: "Exception", reason: exception});
        $delegate(exception, cause);
    };
});
