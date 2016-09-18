(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .controller('FinancialDataDeleteController',FinancialDataDeleteController);

    FinancialDataDeleteController.$inject = ['$uibModalInstance', 'entity', 'FinancialData'];

    function FinancialDataDeleteController($uibModalInstance, entity, FinancialData) {
        var vm = this;
        vm.financialData = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            FinancialData.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
