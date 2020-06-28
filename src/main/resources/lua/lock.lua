-- Set a lock
--  if success，return 1
local key     = KEYS[1]
local content = KEYS[2]
local ttl     = ARGV[1]
local lockSet = redis.call('setnx', key, content)
if lockSet == 1 then
  redis.call('pexpire', key, ttl)
--  redis.call('incr', "count")
else
  -- if the same value，get the existing lock
  local value = redis.call('get', key)
  if(value == content) then
    lockSet = 1;
    redis.call('pexpire', key, ttl)
  end
end
return lockSet
