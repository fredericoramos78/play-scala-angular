define(['angular', 'ngTable', 'jsRoutes'], function(angular, ngTable, jsRoutes) {
    'use strict';
    
    
    return ['$scope', '$http', '$uibModal', 'NgTableParams', function($scope, $http, $uibModal, NgTableParams) {
        
        $scope.errorLoading = false;
        $scope.errorMessage = null;
        
        $scope.form = { filterBy: null };
        
        
        $scope.addCustomer = function() {
            $scope.openModal(null);
        };
        
        $scope.editCustomer = function(customerId) {
            var formData = { _id: customerId };
            $http.post(jsRoutes.controllers.rest.catalog.CustomerController.loadCustomer(customerId).url, angular.toJson(formData))
                .then(function(result) {
                    $scope.openModal(result.data);
                })
                .catch(function(error) { 
                    $scope.errorLoading = true;
                    $scope.errorMessage = error.data;
                });
            
        };
        
        $scope.openModal = function(customerInfo) {
            var modalOptions = { 
                    ariaLabelledBy: 'modal-title',
                    ariaDescribedBy: 'modal-body',
                    templateUrl: 'addEditModal.html',
                    controller: 'CustomerModalCtrl',
                    scope: $scope,
                    resolve: {
                        customer: function() { return customerInfo; }
                    }
                };
                $scope.resetError();
                $uibModal.open(modalOptions)
                    .result
                    .then(function(customerInfo) { 
                          if (customerInfo !== undefined) {
                              var actionUrl = jsRoutes.controllers.rest.catalog.CustomerController.newCustomer().url;
                              if (customerInfo._id !== undefined) {
                                  actionUrl = jsRoutes.controllers.rest.catalog.CustomerController.editCustomer().url;
                              }
                              // cleaning error area
                              $http.post(actionUrl, angular.toJson(customerInfo))
                                  .then(function(result) { 
                                      $scope.tableOptions.reload(); 
                                   })
                                  .catch(function(error) { 
                                            $scope.errorMessage = error.data;
                                            $scope.errorLoading = true;
                                        }
                                  );
                          }
                    });
        };
        
        $scope.resetError = function() {
            $scope.errorLoading = false;
            $scope.errorMessage = null;
        };
        
        $scope.filter = function() {
            $scope.updateTable();
        };
        
        $scope.clearFilter = function() {
            $scope.form.filterBy = null;
            $scope.updateTable();
        };

        $scope.reloadTable = function() {
            $scope.errorMessage = error;
            $scope.errorLoading = true;
            $scope.tableOptions.reload(); 
        };
        
        $scope.updateTable = function() {
            $scope.resetError();
            
            var tableInitParams = { count: 25, };
            var tableInitValues = {
                counts: [25, 50, 100],
                getData: function(params) {
                    var initParams = { offset: (params.page()-1)*params.count(), length: params.count() };
                    if ($scope.form.filterBy !== null) {
                        initParams.search = { value: $scope.form.filterBy };
                    }
                    return $http.post(jsRoutes.controllers.rest.catalog.CustomerController.list().url, angular.toJson(initParams))
                        .then(function(result) {
                            params.total(result.data.recordsFiltered);
                            return result.data.data;
                        })
                        .catch(function(error) { 
                            $scope.errorMessage = error.data;
                            $scope.errorLoading = true;
                        });
                  }
            };
            $scope.tableOptions = new NgTableParams(tableInitParams, tableInitValues);
        };

        
        $scope.init = function() {
            $scope.updateTable();
        };
        $scope.init();
    }];
});
