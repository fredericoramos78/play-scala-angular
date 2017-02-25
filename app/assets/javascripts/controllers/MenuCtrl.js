define(['angular'], function(angular) {
    'use strict';
    
    
    return ['$scope', '$state', function($scope, $state) {
        
        $scope.gotoCustomers = function() { $state.go('customerListing'); };
        $scope.gotoPOs = function() { $state.go('POListing'); };
        $scope.gotoPayments = function() { $state.go('paymentListing'); };
        $scope.gotoInvoices = function() { $state.go('invoiceListing'); };
    }];
});
