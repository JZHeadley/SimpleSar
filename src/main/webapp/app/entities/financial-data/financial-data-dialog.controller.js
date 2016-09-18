(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .controller('FinancialDataDialogController', FinancialDataDialogController);

    FinancialDataDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'FinancialData', 'Student', 'Parent'];

    function FinancialDataDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, FinancialData, Student, Parent) {
        var vm = this;
        vm.financialData = entity;
        vm.students = Student.query({filter: 'financialdata-is-null'});
        $q.all([vm.financialData.$promise, vm.students.$promise]).then(function() {
            if (!vm.financialData.student || !vm.financialData.student.id) {
                return $q.reject();
            }
            return Student.get({id : vm.financialData.student.id}).$promise;
        }).then(function(student) {
            vm.students.push(student);
        });
        vm.parents = Parent.query({filter: 'financialdata-is-null'});
        $q.all([vm.financialData.$promise, vm.parents.$promise]).then(function() {
            if (!vm.financialData.parent || !vm.financialData.parent.id) {
                return $q.reject();
            }
            return Parent.get({id : vm.financialData.parent.id}).$promise;
        }).then(function(parent) {
            vm.parents.push(parent);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('ramHacksApp:financialDataUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.financialData.id !== null) {
                FinancialData.update(vm.financialData, onSaveSuccess, onSaveError);
            } else {
                FinancialData.save(vm.financialData, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
