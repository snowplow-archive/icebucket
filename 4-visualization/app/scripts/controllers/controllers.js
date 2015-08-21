'use strict';

angular.module('snowplow.SprayEvent')

  .controller('MainCtrl', function($rootScope, $location) {
    $rootScope.$path = $location.path.bind($location);
  })
  .controller('EventCtrl', function($scope, $rootScope, $location, $route, Events) {
    $rootScope.$path = $location.path.bind($location);
    $scope.events = Events.query();
    $scope.canEdit = true;
    $scope.canDelete = true;

    $scope.deleteEvent=function(event){
      event.$delete(function(){
        $route.reload();
      });
    };
    $scope.editEvent=function(event){
      $location.path('/events/' + event.id + '/edit');
    };

  })
  .controller('EventViewCtrl', function($scope, $rootScope, $location, $routeParams, Events) {
    $rootScope.$path = $location.path.bind($location);
    $scope.event = Events.get({id:$routeParams.id});
    $scope.canEdit = true;
    $scope.canDelete = true;

    $scope.deleteEvent=function(event){
       event.$delete(function(){
          $location.path('/events');
      });
    };
    $scope.editEvent=function(event){
      $location.path('/events/' + event.id + '/edit');
    };

  })
  .controller('EventEditCtrl', function($scope, $rootScope, $location, $routeParams, Events) {
    $rootScope.$path = $location.path.bind($location);
    $scope.event = Events.get({id:$routeParams.id});
    $scope.canEdit = false;
    $scope.canDelete = true;

    $scope.deleteEvent=function(event){
      event.$delete(function(){
          $location.path('/events');
      });
    };
    $scope.updateEvent=function(){
      $scope.event.$update(function(){
          $location.path('/events/'+$scope.event.id);
      });
    };
  })
  .controller('EventNewCtrl', function($scope, $rootScope, $location, Events) {
    $rootScope.$path = $location.path.bind($location);
    $scope.event = new Events();
    $scope.canEdit = false;
    $scope.canDelete = false;

    function isInt(value) {
      return !isNaN(value) &&
        parseInt(Number(value)) === value &&
        !isNaN(parseInt(value, 10));
    }

    $scope.addEvent=function(){
      if(!isInt($scope.event.count)) $scope.event.count=0;

      $scope.event.$save(function(){
        $location.path('/events');
      });
    };
  });
