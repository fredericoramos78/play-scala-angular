define(['jquery', 'angular', 'ngTable', 'angular-ui-router', 'angular-animate', 'jsRoutes', 'controllers'], function($, angular, moment, jsRoutes) {
    'use strict';

    var app = angular.module('app', [ 'ui.router', 'ngAnimate', 'ngAria', 'ngSanitize', 'ui.bootstrap', 'toastr', 'ngAnimate', 'ngTable', 'controllers' ]);

    /**
     * Angular configurations
     */
    app.config(['$urlRouterProvider', '$locationProvider', '$stateProvider', 
        function($urlRouterProvider, $locationProvider, $stateProvider) {

        $urlRouterProvider.otherwise('/').deferIntercept();

        $stateProvider
            .state('home', 
                   { url: '/home', 
                     templateUrl: '/views/home/landing.html',
                     controller: 'HomeCtrl', })
            .state('customerListing', 
                   { url: '/customer/list', 
                     templateUrl: '/views/customers/listing.html',
                     controller: 'CustomerListingCtrl', })
        ;
        
        $locationProvider.html5Mode(true).hashPrefix("!");
    } ]);

    // ngAnimation settings.
    app.config(['$animateProvider', function($animateProvider){
        // do not mess with 'fa-spin' icons
        $animateProvider.classNameFilter(/^((?!(fa-spin)).)*$/);
    }]);

    // Toastr default settings.
    app.config(['toastrConfig', function(toastrConfig) {
        angular.extend(toastrConfig, { timeOut: 3500 });
    }]);
    
    /**
     * Angular initialization code
     */
    // ui-router initialization and rootScope defaults
    app.run(['$rootScope', '$state', '$stateParams', '$location', '$urlRouter',
    function($rootScope, $state, $stateParams, $location, $urlRouter) {
        // Redirect to home page if user is already authenticated
        $state.go('home');
    }]);
        
 // Filters
    app.filter('isodatetime', ['$filter', function($filter) {
        return function(input, format) {
            var date = moment(input);
            if (date) {
                if (format) {
                    return date.format(format);
                } else {
                    var isToday = moment().isSame(date, 'day');
                    var f = isToday ? '[Hoje,] HH:mm' : 'DD/MM/YYYY HH:mm';
                    return date.format(f);
                }
            } else {
                return '';
            }
        };
    }]);
    
    return app;
});
