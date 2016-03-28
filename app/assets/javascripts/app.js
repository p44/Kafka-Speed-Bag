(function(angular) {
    'use strict';


    angular.module('kspeedbag', ['kspeedbag.controllers', 'ngRoute', 'ngCookies', 'ngSanitize', 'ng.epoch'])
        .config(config)
        .run(run);

    config.$inject = ['$routeProvider', '$locationProvider'];
    function config($routeProvider, $locationProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'assets/html/home.html',
                controller: 'PunchCtrl',
                controllerAs: 'ksb'
            })
            .when('/results', {
                templateUrl: 'assets/html/results.html',
                controller: 'ResultsCtrl',
                controllerAs: 'ksb'
            })
            .otherwise({redirectTo: '/'});

        $locationProvider.html5Mode(true);
    }

    // https://docs.angularjs.org/api/ngCookies/service/$cookies
    run.$inject = ['$rootScope', '$location', '$cookies', '$http'];
    function run($rootScope, $location, $cookies, $http) {
        $rootScope.started = false; // the app is started
        $rootScope.app_name = 'kafkaspeedbag';
        $rootScope.authenticated = true;
        console.log("authenticated: " + $rootScope.authenticated);
    }

    /** services module initialization, allows adding services to module in multiple files */
    angular.module('kspeedbag.services', []);

})(window.angular);
