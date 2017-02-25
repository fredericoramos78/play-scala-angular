define(['angular', './HomeCtrl', './MenuCtrl', 'controllers/catalog'],
    function(angular, HomeCtrl, MenuCtrl) {
    'use strict';
    
        return angular.module('controllers', ['controllers.catalog'])
            .controller('HomeCtrl', HomeCtrl)
            .controller('MenuCtrl', MenuCtrl);
    }
);