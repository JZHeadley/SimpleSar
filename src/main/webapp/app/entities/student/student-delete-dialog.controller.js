(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .controller('StudentDeleteController',StudentDeleteController);

    StudentDeleteController.$inject = ['$uibModalInstance', 'entity', 'Student'];

    function StudentDeleteController($uibModalInstance, entity, Student) {
        var vm = this;
        vm.student = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Student.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
