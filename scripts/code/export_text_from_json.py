import json
import sys

def get_data(filename:str):
  try:
    file_text = json.load(open(f'Q:\\GROUPS\\BR_SC_JGS_WM_LOGISTICA\\PCP\\PPC_AI_Procedures\\ppc_secretary\\indicadores\\{filename}.json', 'r', encoding='utf-8'))
    for text in file_text:
      print(text)

  except:
    print('Erro')

get_data(sys.argv[1])