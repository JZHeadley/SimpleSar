(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('student', {
            parent: 'entity',
            url: '/student',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ramHacksApp.student.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/student/students.html',
                    controller: 'StudentController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('student');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('student-detail', {
            parent: 'entity',
            url: '/student/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ramHacksApp.student.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/student/student-detail.html',
                    controller: 'StudentDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('student');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Student', function($stateParams, Student) {
                    return Student.get({id : $stateParams.id});
                }]
            }
        })
        .state('student.new', {
            parent: 'student',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/student/student-dialog.html',
                    controller: 'StudentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                firstName: null,
                                lastName: null,
                                ssn: null,
                                gender: null,
                                phoneNumber: null,
                                maritalStatus: null,
                                dependent: false,
                                dob: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('student', null, { reload: true });
                }, function() {
                    $state.go('student');
                });
            }]
        })
        .state('student.edit', {
            parent: 'student',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/student/student-dialog.html',
                    controller: 'StudentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Student', function(Student) {
                            return Student.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('student', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('student.delete', {
            parent: 'student',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/student/student-delete-dialog.html',
                    controller: 'StudentDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Student', function(Student) {
                            return Student.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('student', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
