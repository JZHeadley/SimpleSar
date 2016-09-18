(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .controller('ParentDialogController', ParentDialogController);

    ParentDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Parent', 'Student'];

    function ParentDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Parent, Student) {
        var vm = this;
        vm.parent = entity;
        vm.students = Student.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('ramHacksApp:parentUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.parent.id !== null) {
                Parent.update(vm.parent, onSaveSuccess, onSaveError);
            } else {
                Parent.save(vm.parent, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
