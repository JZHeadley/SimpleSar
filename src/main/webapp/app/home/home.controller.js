(function () {
    'use strict';

    angular
        .module('ramHacksApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'Student'];

})();
function HomeController($scope, Principal, LoginService, $state, Student) {
    var vm = this;

    // vm.student = Student;
    vm.account = null;
    vm.isAuthenticated = null;
    vm.login = LoginService.open;
    vm.register = register;
    $scope.$on('authenticationSuccess', function () {
        getAccount();
    });
    console.log(Student);

    vm.students = [];
    vm.loadAll = function () {
        Student.query(function (result) {
            vm.students = result;
        });
    };
    vm.loadAll();

    console.log(vm.students);

    getAccount();

    function getAccount() {
        Principal.identity().then(function (account) {
            vm.account = account;
            vm.isAuthenticated = Principal.isAuthenticated;
        });
    }

    function register() {
        $state.go('register');
    }
}
