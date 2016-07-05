// Copyright 2013, NICT
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of NICT nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
/**
 * @file MCMLSampleApplicationAppDelegate.mm
 * @brief  Implementation of application delegate class
 */

#import "MCMLSampleApplicationAppDelegate.h"
#import "MCMLSampleApplicationViewController.h"

@implementation MCMLSampleApplicationAppDelegate


@synthesize window = _window;                   //!< @brief Main window
@synthesize viewController = _viewController;   //!< @brief  View controller
@synthesize mcmlLock = _mcmlLock;               //!< @brief Object for simultaneous access block from multiple threads

/**
 * @brief  Called when application is started
 * @param application  Application
 * @param launchOptions  Normally:nil  When called from other applications:NSDictionary
 * @return YES
 */
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
	// Override point for customization after application launch.
	_mcmlLock = [NSObject alloc];
	self.window.rootViewController = _viewController;
	[self.window makeKeyAndVisible];
	return YES;
}

/**
 * @brief  Called just before application is deactivated
 * @param application  Application
 */
- (void)applicationWillResignActive:(UIApplication *)application {
	/*
     Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
     Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
	 */
}

/**
 * @brief  Called whine application is deactivated and in the background
 * @param application  Application
 */
- (void)applicationDidEnterBackground:(UIApplication *)application {
	/*
     Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
     If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
	 */
}

/**
 * @brief  Called when background application is started
 * @param application  Application
 */
- (void)applicationWillEnterForeground:(UIApplication *)application {
	/*
     Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
	 */
}

/**
 * @brief  Called when application is activated
 * @param application  Application
 */
- (void)applicationDidBecomeActive:(UIApplication *)application {
	/*
     Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
	 */
}

/**
 * @brief  Called when attempts are made to end application
 * @param application  Application
 */
- (void)applicationWillTerminate:(UIApplication *)application {
	/*
     Called when the application is about to terminate.
     Save data if appropriate.
     See also applicationDidEnterBackground:.
	 */
}

/**
 * @brief  Instance release method
 */
- (void)dealloc {
	[_mcmlLock release];
	[_window release];
	[_viewController release];
	[super dealloc];
}

@end
