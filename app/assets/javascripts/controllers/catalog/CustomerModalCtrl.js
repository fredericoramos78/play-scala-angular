define(['angular', 'ngTable', 'jsRoutes'], function(angular, ngTable, jsRoutes) {
    'use strict';
    
    
    return ['$scope', '$http', '$uibModalInstance', 'customer', function($scope, $http, $uibModalInstance, customer) {
        
        $scope.currentCustomer = { };
        $scope.customerNameLen = 10;
        
        $scope.modalOk = function() {
            $uibModalInstance.close( angular.copy($scope.currentCustomer) );
        };

        $scope.modalCancel = function() {
            $uibModalInstance.dismiss();
        };
        
        $scope.isNameAcceptable = function() {
            return ($scope.customerNameLen >= 10); 
        };
        
        $scope.updateCustomerLen = function() {
            $scope.customerNameLen = ($scope.currentCustomer.name === undefined ? 0 : $scope.currentCustomer.name.length);
        };
        
        $scope.init = function() {
            if (customer !== null) {
                $scope.currentCustomer = { 
                    _id: customer._id || null,
                    name: customer.name,
                    taxId: customer.taxId
                };
            }
        };
        $scope.init();
    }];
});
