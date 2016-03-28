(function (angular) {
    'use strict';

    /** Controllers 1 */
    angular.module('kspeedbag.controllers', ['kspeedbag.services'])
        .controller('PunchCtrl', ['$route', '$routeParams', '$location', '$scope', '$rootScope', '$http',
            function ($route, $routeParams, $location, $scope, $rootScope, $http) {
                $rootScope.pageTitle = 'Welcome';
                this.$route = $route;
                this.$location = $location;
                this.$routeParams = $routeParams;

                $scope.appNotStarted = function () { return !($rootScope.started); };

                // start generating and feeding into kafka
                $scope.startSimulation = function () {
                    var url = '/start';
                    $http({method: 'GET', url: url
                    }).success(function(data, status, headers, config) {
                        console.log(url);
                        console.log(data);
                        $rootScope.started = true;
                    }).error(function(data, status, headers, config) {
                        console.log('GET ' + url + ' ERROR ' + status);
                    });
                };

            }])
        .controller('ResultsCtrl', ['$route', '$routeParams', '$location', '$scope', '$rootScope', '$http',
            function ($route, $routeParams, $location, $scope, $rootScope, $http) {
                this.$route = $route;
                this.$location = $location;
                this.$routeParams = $routeParams;

                $scope.result_json = '';
                $scope.leader = { name: '', score: 0 };
                $scope.colors_counts = [];

                // http://epochjs.github.io/epoch/real-time/#gauge
                // http://dainbrump.github.io/ng-epoch/
                $scope.gauge_red_value=0.0;
                $scope.gauge_orange_value=0.0;
                $scope.gauge_yellow_value=0.0;
                $scope.gauge_green_value=0.0;
                $scope.gauge_blue_value=0.0;
                $scope.gauge_indigo_value=0.0;

                $scope.appStarted = function () { return $rootScope.started; };

                $scope.setColorGuages = function () {
                    var len = $scope.colors_counts.length;
                    for (var i = 0; i < len; i++) {
                        var x = $scope.colors_counts[i];
                        console.log(x.name + '  ' + x.value);
                        if ('red' === x.name) $scope.gauge_red_value = x.value;
                        else if ('orange' === x.name) $scope.gauge_orange_value = x.value;
                        else if ('yellow' === x.name) $scope.gauge_yellow_value = x.value;
                        else if ('green' === x.name) $scope.gauge_green_value = x.value;
                        else if ('blue' === x.name) $scope.gauge_blue_value = x.value;
                        else if ('indigo' === x.name) $scope.gauge_indigo_value = x.value;
                    }
                };

                /** handle incoming delivery feed messages: add to messages array */
                $scope.addResultFeedMsg = function (msg) {
                    var msgObj = JSON.parse(msg.data);
                    console.log('Received Result msgObj' + msgObj);
                    $scope.$apply(function () {
                        $scope.result_json = msg.data;
                        console.log('Received Result  msg.data ' + msg.data);
                        $scope.leader = msgObj.leader;
                        $scope.colors_counts = msgObj.colorCountList;
                        $scope.setColorGuages();
                    });
                };

                /** start listening to the deliery feed for the fish store */
                $scope.listen = function () {
                    $scope.delivery_feed = new EventSource("/results/feed");
                    $scope.delivery_feed.addEventListener("message", $scope.addResultFeedMsg, false);
                };

                $scope.listen();

            }]);


})(window.angular);





