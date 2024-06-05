import json
import sys

def add_item(question:str, answer:str):
  try:
    inteligence = json.load(open('Q:\\GROUPS\\BR_SC_JGS_WM_LOGISTICA\\PCP\\Central\\__Automaticos_Python\\06-find_fles\\inteligence.json', 'r', encoding='utf-8'))
    inteligence.append({"Question": question, "Answer": answer})
    json.dump(inteligence, open('Q:\\GROUPS\\BR_SC_JGS_WM_LOGISTICA\\PCP\\Central\\__Automaticos_Python\\06-find_fles\\inteligence.json', 'w', encoding='utf-8'), indent=4)
    
  except FileNotFoundError:
    print("Arquivo inteligence.json não encontrado. Criando um novo arquivo.")
    with open('Q:\\GROUPS\\BR_SC_JGS_WM_LOGISTICA\\PCP\\Central\\__Automaticos_Python\\06-find_fles\\inteligence.json', 'w', encoding='utf-8') as f:
      json.dump([{"Question": question, "Answer": answer}], f, indent=4)


question = sys.argv[1]
answer = sys.argv[2]
add_item(question, answer)