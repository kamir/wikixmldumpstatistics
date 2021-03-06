import pprint # Used for formatting the output for viewing, not necessary for most code

from wikitools import wiki, api

site = wiki.Wiki("http://sv.wikipedia.org/w/api.php")

params = {'action':'query',
    'meta':'siteinfo',
    'siprop':'general'
}

#req = api.APIRequest(site, params)
#res = req.query(querycontinue=False)
#pprint.pprint(res)


params = {'action':'sitematrix'}

#req = api.APIRequest(site, params)
#res = req.query(querycontinue=False)
#pprint.pprint(res)


# sample A
#
#   Get first 5 revisions of the "Main Page" made after 2006-05-01
#
#       api.php?action=query&prop=revisions&titles=Main%20Page&rvlimit=5&rvprop=timestamp|user|
#       comment&rvdir=newer&rvstart=20060501000000


params = {'action':'query' ,
'prop':'revisions',
'titles':'Sydafrika',
'rvlimit':'1',
'rvprop':'timestamp|user|comment',
'rvdir':'newer',
'rvstart':'20050501000000' 
}


req = api.APIRequest(site, params)
res = req.query(querycontinue=False)
pprint.pprint(res)

params = {'action':'query' ,
'prop':'iwlinks',
'titles':'Sydafrika',
}


req = api.APIRequest(site, params)
res = req.query(querycontinue=False)
pprint.pprint(res)

params = {'action':'query' ,
'prop':'links',
'titles':'Sydafrika',
}


req = api.APIRequest(site, params)
res = req.query(querycontinue=False)
pprint.pprint(res)

params = {'action':'query' ,
'prop':'langlinks',
'titles':'Sydafrika',
}


req = api.APIRequest(site, params)
res = req.query(querycontinue=False)
pprint.pprint(res)




params = {'action':'query' ,
'list':'users',
'ususers':'YurikBot',
'usprop':'groups'
}
req = api.APIRequest(site, params)
res = req.query(querycontinue=False)
pprint.pprint(res)

