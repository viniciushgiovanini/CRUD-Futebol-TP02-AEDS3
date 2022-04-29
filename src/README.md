# Detalhes da Branch master

- Essa é a branch **PRINCIPAL**

## Melhorias Versão [0.4]

- O programa realiza a primeira etapa da ordenação externa, separando os 10 primeiros indices em um array de objeto, e mandando para o quicksort voltando ordenado e salvando ele no ARQ1, de 10 em 10.

## Pioras

- Tem que arrumar de pesquisa, pois quando escreve valores impares (sem contar o 0), ele ta deixando um gap de 13 0 atrapalhando a ordenação, temos que fazer a correcao na pesquisa, pois na escrita sempre pode aparecer mais registros.(vou fazer isso testando se depois do primeiro 0 tem algum short com mais 0, se tiver faço a correção.)
- Ele só faz a pesquisa no quicksort e o salvamento no arq1 quando tem 10 elementos no array de objeto indice.

## Próximos Passos

- Ordenacao externa incompleta.
