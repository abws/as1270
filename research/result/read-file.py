f = open("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/result/results.txt", "r")
array = []
for x in f:
    x = x.strip()
    x = float(x)
    array.append(x)

print(array) 