(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .controller('FinancialDataDetailController', FinancialDataDetailController);

    FinancialDataDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'FinancialData', 'Student', 'Parent'];

    function FinancialDataDetailController($scope, $rootScope, $stateParams, entity, FinancialData, Student, Parent) {
        var vm = this;
        vm.financialData = entity;
        
        var unsubscribe = $rootScope.$on('ramHacksApp:financialDataUpdate', function(event, result) {
            vm.financialData = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
