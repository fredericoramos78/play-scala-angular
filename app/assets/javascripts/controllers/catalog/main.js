define(['angular', './CustomerListingCtrl', './CustomerModalCtrl'],
    function(angular, CustomerListingCtrl, CustomerModalCtrl) {
    'use strict';
    
        return angular.module('controllers.catalog', [])
            .controller('CustomerListingCtrl', CustomerListingCtrl)
            .controller('CustomerModalCtrl', CustomerModalCtrl);
    }
);