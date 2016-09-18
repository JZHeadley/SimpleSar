(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .controller('StudentController', StudentController);

    StudentController.$inject = ['$scope', '$state', 'Student', 'StudentSearch'];

    function StudentController ($scope, $state, Student, StudentSearch) {
        var vm = this;
        vm.students = [];
        vm.loadAll = function() {
            Student.query(function(result) {
                vm.students = result;
            });
        };


        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            StudentSearch.query({query: vm.searchQuery}, function(result) {
                vm.students = result;
            });
        };
        vm.loadAll();

    }
})();
