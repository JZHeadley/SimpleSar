(function() {
    'use strict';

    angular
        .module('ramHacksApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('financial-data', {
            parent: 'entity',
            url: '/financial-data',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ramHacksApp.financialData.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/financial-data/financial-data.html',
                    controller: 'FinancialDataController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('financialData');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('financial-data-detail', {
            parent: 'entity',
            url: '/financial-data/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ramHacksApp.financialData.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/financial-data/financial-data-detail.html',
                    controller: 'FinancialDataDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('financialData');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'FinancialData', function($stateParams, FinancialData) {
                    return FinancialData.get({id : $stateParams.id});
                }]
            }
        })
        .state('financial-data.new', {
            parent: 'financial-data',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/financial-data/financial-data-dialog.html',
                    controller: 'FinancialDataDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                outstandingLoands: null,
                                efcTotal: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('financial-data', null, { reload: true });
                }, function() {
                    $state.go('financial-data');
                });
            }]
        })
        .state('financial-data.edit', {
            parent: 'financial-data',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/financial-data/financial-data-dialog.html',
                    controller: 'FinancialDataDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['FinancialData', function(FinancialData) {
                            return FinancialData.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('financial-data', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('financial-data.delete', {
            parent: 'financial-data',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/financial-data/financial-data-delete-dialog.html',
                    controller: 'FinancialDataDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['FinancialData', function(FinancialData) {
                            return FinancialData.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('financial-data', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
