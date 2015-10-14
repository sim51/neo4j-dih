'use strict';

app.directive('spinner', function() {
        return {
            restrict: 'A',
            replace: true,
            transclude: true,
            scope: {
                loading: '=spinner'
            },
            templateUrl: 'assets/directives/spinner.html',
            link: function(scope, element, attrs) {
                var spinner = new Spinner({scale:3, top: '50%', left:'50%'}).spin();
                var loadingContainer = document.getElementsByClassName('loading-spinner-container')[0];
                loadingContainer.appendChild(spinner.el);
            }
        };
    });
