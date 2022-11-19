var responsavelProtocoloCtrl = function ($scope, $modalInstance, $injector, $rootScope,  growl, srvRest) {
        
        $injector.invoke(selectCtrl, this, {$scope: $scope});
        
        $scope.data = {};

    $scope.validateForm = function(){
             return detranUtil.ehBrancoOuNulo($scope.data.nome)
                    || detranUtil.ehBrancoOuNulo($scope.data.cpf)
                    || detranUtil.ehBrancoOuNulo($scope.data.descricaoDocumento)
                    || detranUtil.ehBrancoOuNulo($scope.data.numeroDocumentoRepresentacao)
                    || detranUtil.ehBrancoOuNulo($scope.data.dataDocumento);
        };
        
        $scope.ok = function (){
                
            $modalInstance.close($scope.data);
        };
        
        $scope.cancel = function(){
            $modalInstance.dismiss();
        };
        
};
responsavelProtocoloCtrl['$inject'] = ["$scope", "$modalInstance", "$injector", "$rootScope", "growl", "srvDetranAbstractResourceRest"];