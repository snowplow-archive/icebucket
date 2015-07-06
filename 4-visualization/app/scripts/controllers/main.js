'use strict';

angular.module('snowplow.SprayEvent')

  .controller('MainCtrl', function($scope, $location, version) {
    $scope.$path = $location.path.bind($location);
    $scope.version = version;
  })
  .controller('EventCtrl', function($scope, $location, version, GetEvents) {
    $scope.$path = $location.path.bind($location);
    $scope.version = version;
    $scope.data = {};

    GetEvents.query(function(response) {
      $scope.data.events = response;
    });

  });
