'use strict';

angular.module('snowplow.SprayEvent', ['ngAnimate', 'ngRoute','ngResource'])

  .config(function($locationProvider, $routeProvider) {

    $locationProvider.html5Mode(false);

    $routeProvider
      .when('/', {
        templateUrl: 'views/home.html',
        controller: 'MainCtrl'
      })
      .when('/events', {
        templateUrl: 'views/events.html',
        controller: 'EventCtrl'
      })
      .when('/events/new', {
        templateUrl: 'views/events-new.html',
        controller: 'EventNewCtrl'
      })
      .when('/events/:id', {
        templateUrl: 'views/events-view.html',
        controller: 'EventViewCtrl'
      })
      .when('/events/:id/edit', {
        templateUrl: 'views/events-edit.html',
        controller: 'EventEditCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });

  });
