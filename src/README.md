# Detalhes da Branch master

- Essa é a branch **PRINCIPAL**

## Melhorias Versão [0.6.8]


- O problema do update com embaralhamento foi resolvido, agora assimq eu for realizado um novo update e precisar criar um novo id, ele não irá escrever direto do arquivo indice da funcao update, ele ira mandar para a funcao write indice da classe indice, entrando assim nas regras de embaralhamento.
- O arquivo realiza ordenacao e pesquisa com OE. Não foi testado quando a OE tenta ordenar mais de 80 caracteres correspondendte ao ar1 2 3 e 4 cheios tendo que alocar no arq 1 novamente.

## OBS
- Versão 0.6.5 tem uma outra alternativa para concerta o problema da ordenacao pos update diferente dessa feita agora

## Próxima melhoria (Problemas) 
- Testar Create Read Update Delete principais. 
- Testar OE com 80 caracteres.  
   --X--  
- Fazer Lista invertida  
- Juntar CRUD com a lista invertida.  
## Próximos Passos

- Ordenacao externa final.  
- Criacao do resto do Crud. (UPDATE E DELETE).   
- Lista invertida.  
