'use strict';

angular.module('snowplow.SprayEvent')
  .factory('Events', function($resource){
    return $resource('api/events/:id', { id: '@id'}, {
      update: {
        method: 'PUT'
      }
    });
  });
