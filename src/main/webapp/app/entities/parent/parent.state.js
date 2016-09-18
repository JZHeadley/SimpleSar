(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('parent', {
            parent: 'entity',
            url: '/parent',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ramHacksApp.parent.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/parent/parents.html',
                    controller: 'ParentController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('parent');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('parent-detail', {
            parent: 'entity',
            url: '/parent/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ramHacksApp.parent.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/parent/parent-detail.html',
                    controller: 'ParentDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('parent');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Parent', function($stateParams, Parent) {
                    return Parent.get({id : $stateParams.id});
                }]
            }
        })
        .state('parent.new', {
            parent: 'parent',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/parent/parent-dialog.html',
                    controller: 'ParentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                lastName: null,
                                middleInitial: null,
                                ssn: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('parent', null, { reload: true });
                }, function() {
                    $state.go('parent');
                });
            }]
        })
        .state('parent.edit', {
            parent: 'parent',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/parent/parent-dialog.html',
                    controller: 'ParentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Parent', function(Parent) {
                            return Parent.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('parent', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('parent.delete', {
            parent: 'parent',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/parent/parent-delete-dialog.html',
                    controller: 'ParentDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Parent', function(Parent) {
                            return Parent.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('parent', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
