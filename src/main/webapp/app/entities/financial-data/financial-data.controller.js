(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .controller('FinancialDataController', FinancialDataController);

    FinancialDataController.$inject = ['$scope', '$state', 'FinancialData', 'FinancialDataSearch'];

    function FinancialDataController ($scope, $state, FinancialData, FinancialDataSearch) {
        var vm = this;
        vm.financialData = [];
        vm.loadAll = function() {
            FinancialData.query(function(result) {
                vm.financialData = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            FinancialDataSearch.query({query: vm.searchQuery}, function(result) {
                vm.financialData = result;
            });
        };
        vm.loadAll();
        
    }
})();
