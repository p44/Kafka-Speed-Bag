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

                $scope.appStarted = function () { return $rootScope.started; };

                /** handle incoming delivery feed messages: add to messages array */
                $scope.addResultFeedMsg = function (msg) {
                    var msgObj = JSON.parse(msg.data);
                    console.log('Received Result msgObj' + msgObj);
                    $scope.$apply(function () {
                        $scope.result_json = msg.data;
                        $scope.leader = msgObj.leader;
                        console.log('Received Result $scope.leader ' + $scope.leader);
                    });
                };

                /*
                 data: {"leader":{"name":"Amit","value":98},"colorCountList":[{"name":"yellow","value":8},{"name":"orange","value":3},{"name":"green","value":3},{"name":"violet","value":3},{"name":"blue","value":1},{"name":"red","value":2}]}
                 */

                /** start listening to the deliery feed for the fish store */
                $scope.listen = function () {
                    $scope.delivery_feed = new EventSource("/results/feed");
                    $scope.delivery_feed.addEventListener("message", $scope.addResultFeedMsg, false);
                };

                $scope.listen();

            }]);


})(window.angular);





