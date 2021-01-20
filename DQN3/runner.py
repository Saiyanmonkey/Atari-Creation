
import numpy as np
import argparse
import timeit
from agent_dqn import Agent_DQN
from environment import Environment
import tensorflow as tf
tf.logging.set_verbosity(tf.logging.ERROR)

seed = 11037


def parse():
	parser = argparse.ArgumentParser(description="runner")
	parser.add_argument('--env_name', default=None, help='environment name')
	parser.add_argument('--train_dqn', action='store_true', help='whether train DQN')
	parser.add_argument('--test_dqn', action='store_true', help='whether test DQN')
	parser.add_argument('--video_dir', default=None, help='output video directory')
	parser.add_argument('--do_render', action='store_true', help='whether render environment')
	args = parser.parse_args()
	return args


def run(args):
	if args.train_dqn:
		env_name = args.env_name or 'BreakoutNoFrameskip-v4'
		env = Environment(env_name, args, atari_wrapper=True)
		agent = Agent_DQN(env, args)
		agent.train()
		stop_time = timeit.default_timer()
		print('Time: ', stop_time - start_time)


	if args.test_dqn:
		env = Environment('BreakoutNoFrameskip-v4', args, atari_wrapper=True, test=True)
		agent = Agent_DQN(env, args)
		test(agent, env)


def test(agent, env, total_episodes=50):
	rewards = []
	score = 0.0
	env.seed(seed)

	for i in range(total_episodes):
		state = env.reset()
		done = False
		# playing one game
		while not done:
			action = agent.make_action(state, test=True)
			state, reward, done, info,  = env.step(action)

			if info['ale.lives']>0:
				score+=reward

		if info['ale.lives']==0:
			print(score)
			rewards.append(score)
			score=0.0



	stop_time = timeit.default_timer()
	print('Run %d episodes'%(total_episodes/5))
	print('Mean:', np.mean(rewards))
	for i in range(len(rewards)):
		print(i," Score ",rewards[i] )
	print('Min: ',np.min(rewards))
	print('Max: ',np.max(rewards))
	print('Time: ', stop_time - start_time)
	plot(rewards)

def plot(rewards):
	import matplotlib.pyplot as plt
	avg_rwd = []
	for i in range(len(rewards)):
		#if i < 30:
			avg_rwd.append(np.mean(rewards[:i+1]))
		#else:
			#avg_rwd.append(np.mean(rewards[i - 30:i]))
	plt.plot(np.arange(len(avg_rwd)), avg_rwd)
	plt.ylabel('Average Score Episodes')
	plt.xlabel('Number of Games')
	plt.show()




if __name__ == '__main__':
	args = parse()
	start_time = timeit.default_timer()
	run(args)
