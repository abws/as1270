f = open("/Users/abdiwahabsalah/Documents/GitLab/as1270/research/result/results.txt", "r")
x = f.readline()
x = x.strip().split()
y = [float(element) for element in x]
print(y)  